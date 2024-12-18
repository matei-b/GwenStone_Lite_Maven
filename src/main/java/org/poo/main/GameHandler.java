package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class GameHandler {
    private static final int BACK_ROW_P1 = 0;
    private static final int FRONT_ROW_P1 = 1;
    private static final int FRONT_ROW_P2 = 2;
    private static final int BACK_ROW_P2 = 3;

    private final  ArrayNode output;
    private final Input inputData;

    private ObjectMapper mapper = new ObjectMapper();

    private  Deck playerOneDeck;
    private  Deck playerTwoDeck;
    private  CardInput playerOneHero;
    private  CardInput playerTwoHero;
    private GameInput game;
    private Player[] players = new Player[2];
    private ArrayList<ActionsInput> actions;
    private int startingPlayer;
    private int currentPlayer;
    private int roundNumber = 0;
    private Table table = new Table();
    private Game gameStatistics;

    /***
     * Constructor to initialize each object and variable used.
     *
     * @param game GameInput
     * @param inputData Input
     * @param output ArrayNode
     * @param gameStatistics Game
     */
    public GameHandler(final GameInput game, final Input inputData,
                       final ArrayNode output, final Game gameStatistics) {
        this.game = game;
        this.gameStatistics = gameStatistics;
        int playerOneDeckIdx = game.getStartGame().getPlayerOneDeckIdx();
        int playerTwoDeckIdx = game.getStartGame().getPlayerTwoDeckIdx();
        playerOneDeck = new Deck(inputData.getPlayerOneDecks().getDecks().get(playerOneDeckIdx),
                                 game.getStartGame().getShuffleSeed());
        playerTwoDeck = new Deck(inputData.getPlayerTwoDecks().getDecks().get(playerTwoDeckIdx),
                                 game.getStartGame().getShuffleSeed());
        playerOneHero = game.getStartGame().getPlayerOneHero();
        playerTwoHero = game.getStartGame().getPlayerTwoHero();
        startingPlayer = game.getStartGame().getStartingPlayer();
        players[0] = new Player(1, playerOneDeck, playerOneHero,
                                table.getTable().get(2), table.getTable().get(3),
                                game.getStartGame().getShuffleSeed());
        players[1] = new Player(2, playerTwoDeck, playerTwoHero,
                                table.getTable().get(1), table.getTable().get(0),
                                game.getStartGame().getShuffleSeed());
        actions = game.getActions();
        this.inputData = inputData;
        this.output = output;
    }

    /***
     * For each round that starts we reset the turn status for each player,
     * give the next card in each player's hand,
     * give the corresponding mana value,
     * and reset the cards and heroes attack status.
     */
    private void startRound() {
        currentPlayer = startingPlayer;
        roundNumber++;

        players[0].setStatusTurn(0);
        players[1].setStatusTurn(0);

        if (!players[0].getPlayerDeck().getDeck().isEmpty()) {
            players[0].addToPlayerHand();
        }
        if (!players[1].getPlayerDeck().getDeck().isEmpty()) {
            players[1].addToPlayerHand();
        }

        players[0].setPlayerMana(min(roundNumber, 10) + players[0].getPlayerMana());
        players[1].setPlayerMana(min(roundNumber, 10) + players[1].getPlayerMana());

        table.unattackedAllCards();
        players[0].getPlayerHero().setHasAttacked(false);
        players[1].getPlayerHero().setHasAttacked(false);
    }

    /***
     * Set the turn status of the player that has last ended his turn,
     * unfreeze his cards on table,
     * update the index of the current player to attack.
     * If both players ended their turns, we start a new round.
     *
     * @param action ActionsInput
     */
    private void endPlayerTurn(final ActionsInput action) {
        int pastPlayer = currentPlayer - 1;
        players[pastPlayer].setStatusTurn(1);

        if ((pastPlayer) == 0) {
            table.unfreezePlayerOneCards();
        } else {
            table.unfreezePlayerTwoCards();
        }

        if (currentPlayer == 1) {
            currentPlayer++;
        } else {
            currentPlayer--;
        }

        if (players[0].getStatusTurn() == 1 && players[1].getStatusTurn() == 1) {
            startRound();
        }
    }

    /***
     * Update the list of cards that are in the placing player's hand,
     * determine if the player has enough mana to place the card and if
     * there is enough space on the corresponding row to place it.
     * If the conditions are met, we place the card on the table
     * and take it from the hand, and subtract the mana used from
     * the total mana pool.
     *
     * @param player Player
     * @param handIdx int
     */
    private void placeCard(final Player player, final int handIdx) {
        ArrayList<Card> cardsInHand = player.getPlayerCardsInHand();
        Card card = cardsInHand.get(handIdx);
        List<Card> row;

        if (card.getDruidStatus() || card.getTankStatus()) {
            row = player.getFrontRow();
        } else {
            row = player.getBackRow();
        }

        ObjectNode placeNode = mapper.createObjectNode();

        placeNode.put("command", "placeCard");
        placeNode.put("handIdx", handIdx);

        if (player.getPlayerMana() < card.getMana()) {
            placeNode.put("error", "Not enough mana to place card on table.");
            output.add(placeNode);
        } else if (row.size() == 5) {
            placeNode.put("error", "Cannot place card on table since row is full.");
            output.add(placeNode);
        } else {
            row.add(cardsInHand.remove(handIdx));
            player.setPlayerMana(player.getPlayerMana() - card.getMana());
        }
    }

    /***
     * Based on the given coordinate determine if the attacked player
     * has any tanks on his front row.
     *
     * @param x the x coordinate of the attacker's card
     * @return boolean value that indicates if a tank is on the front row
     */
    private boolean isTankOnFrontRow(final int x) {
        boolean ok = false;
        int frontRowIdx = 0;

        if (x > 1) {
            frontRowIdx = 1;
        } else {
            frontRowIdx = 2;
        }

        if (!table.getTable().get(frontRowIdx).isEmpty()) {
            for (Card card : table.getTable().get(frontRowIdx)) {
                if (card.getTankStatus()) {
                    ok = true;
                    break;
                }
            }
        }

        return ok;
    }

    /***
     * Checks if the front row of the attacked player is clear.
     *
     * @param x x coordinate of the attacked player's card
     * @param cardAttacked card attacked object
     * @return boolean value that indicates if the front row is clear
     */
    private boolean isFrontRowClear(final int x, final Card cardAttacked) {
        boolean ok = true;
        int frontRowIdx = 0;

        if (x > 1) {
            frontRowIdx = 2;
        } else {
            frontRowIdx = 1;
        }

        if (!cardAttacked.getTankStatus() && !table.getTable().get(frontRowIdx).isEmpty()) {
            for (Card card : table.getTable().get(frontRowIdx)) {
                if (card.getTankStatus()) {
                    ok = false;
                    break;
                }
            }
        }
        return ok;
    }

    /***
     * Checks if the ability cast conditions for each hero are met.
     *
     * @param cardName string
     * @param currentPlayerIdx int
     * @param affectedRow int
     * @return boolean
     */
    private boolean checkAbilityCastConditions(final String cardName,
                                               final int currentPlayerIdx,
                                               final int affectedRow) {
        boolean ok = false;

        if (cardName.equals("Lord Royce") || cardName.equals("Empress Thorina")) {
            if (currentPlayerIdx == 0 && affectedRow < 2) {
                ok = true;
            }
            if (currentPlayerIdx == 1 && affectedRow > 1) {
                ok = true;
            }
        } else if (cardName.equals("General Kocioraw") || cardName.equals("King Mudface")) {
            if (currentPlayerIdx == 0 && affectedRow > 1) {
                ok = true;
            }
            if (currentPlayerIdx == 1 && affectedRow < 2) {
                ok = true;
            }
        }

        return ok;
    }

    /**
     * Processes the "cardUsesAttack" command,
     * allowing a card to attack another card if valid conditions are met.
     * Adds error messages to the output if the action is invalid.
     *
     * @param action The action containing coordinates of the attacker and the attacked card.
     */
    private void cardUsesAttack(final ActionsInput action) {
        Coordinates cardAttackerCoords = action.getCardAttacker();
        Coordinates cardAttackedCoords = action.getCardAttacked();
        int xAttacker = cardAttackerCoords.getX();
        int yAttacker = cardAttackerCoords.getY();
        int xAttacked = cardAttackedCoords.getX();
        int yAttacked = cardAttackedCoords.getY();

        String command = "cardUsesAttack";

        Card cardAttacker = table.getTable().get(xAttacker).get(yAttacker);
        Card cardAttacked = table.getTable().get(xAttacked).get(yAttacked);

        if (table.isFriendlyFire(cardAttackerCoords, cardAttackedCoords)) {
            ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                               cardAttackerCoords,
                                                               cardAttackedCoords);
            attackErrorNode.put("error", "Attacked card does not belong to the enemy.");
            output.add(attackErrorNode);
        } else if (cardAttacker.getAttackStatus()) {
            ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                               cardAttackerCoords,
                                                               cardAttackedCoords);
            attackErrorNode.put("error", "Attacker card has already attacked this turn.");
            output.add(attackErrorNode);
        } else if (cardAttacker.getFrozenStatus()) {
            ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                               cardAttackerCoords,
                                                               cardAttackedCoords);
            attackErrorNode.put("error", "Attacker card is frozen.");
            output.add(attackErrorNode);
        } else if (!isFrontRowClear(cardAttackedCoords.getX(), cardAttacked)) {
                ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                                   cardAttackerCoords,
                                                                   cardAttackedCoords);
                attackErrorNode.put("error", "Attacked card is not of type 'Tank'.");
                output.add(attackErrorNode);
        } else {
            cardAttacker.setHasAttacked(true);

            if (cardAttacked.getHealth() - cardAttacker.getAttackDamage() <= 0) {
                table.getTable().get(cardAttackedCoords.getX()).remove(cardAttackedCoords.getY());
            } else {
                cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.getAttackDamage());
            }
        }
    }

    /**
     * Processes the "cardUsesAbility" command,
     * enabling a card to use its ability against another card.
     * Handles validation and outputs error messages if the action is not allowed.
     *
     * @param action The action containing coordinates
     *               of the card using the ability and the target card.
     */
    private void cardUsesAbility(final ActionsInput action) {
        Coordinates cardAttackerCoords = action.getCardAttacker();
        Coordinates cardAttackedCoords = action.getCardAttacked();
        int xAttacker = cardAttackerCoords.getX();
        int yAttacker = cardAttackerCoords.getY();
        int xAttacked = cardAttackedCoords.getX();
        int yAttacked = cardAttackedCoords.getY();

        String command = "cardUsesAbility";

        Card cardAttacker = table.getTable().get(xAttacker).get(yAttacker);
        Card cardAttacked = table.getTable().get(xAttacked).get(yAttacked);

        if (cardAttacker.getAttackStatus()) {
            ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                               cardAttackerCoords,
                                                               cardAttackedCoords);
            attackErrorNode.put("error", "Attacker card has already attacked this turn.");
            output.add(attackErrorNode);
        } else if (cardAttacker.getFrozenStatus()) {
            ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                               cardAttackerCoords,
                                                               cardAttackedCoords);
            attackErrorNode.put("error", "Attacker card is frozen.");
            output.add(attackErrorNode);
        } else if (cardAttacker.isNotDisciple()) {
            if (table.isFriendlyFire(cardAttackerCoords, cardAttackedCoords)) {
                ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                                   cardAttackerCoords,
                                                                   cardAttackedCoords);
                attackErrorNode.put("error", "Attacked card does not belong to the enemy.");
                output.add(attackErrorNode);
            } else if (!isFrontRowClear(xAttacked, cardAttacked)) {
                    ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                                       cardAttackerCoords,
                                                                       cardAttackedCoords);
                    attackErrorNode.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(attackErrorNode);
            } else {
                cardAttacker.setHasAttacked(true);

                cardAttacker.applyCardAbility(table, cardAttackedCoords);

                if (cardAttacked.getHealth() == 0) {
                    table.getTable().get(xAttacked).remove(yAttacked);
                }
            }
        } else if (!table.isFriendlyFire(cardAttackerCoords, cardAttackedCoords)) {
            ObjectNode attackErrorNode = createCoordsErrorNode(command,
                                                               cardAttackerCoords,
                                                               cardAttackedCoords);
            attackErrorNode.put("error", "Attacked card does not belong to the current player.");
            output.add(attackErrorNode);
        } else {
            cardAttacker.setHasAttacked(true);

            cardAttacker.applyCardAbility(table, cardAttackedCoords);

            if (cardAttacked.getHealth() == 0) {
                table.getTable().get(cardAttackedCoords.getX()).remove(cardAttackedCoords.getY());
            }
        }

    }

    /**
     * Processes the "useAttackHero" command, where a card attacks the enemy's hero.
     * Updates hero health and manages game state if the hero's health reaches zero.
     *
     * @param action The action containing the coordinates of the attacking card.
     */
    private void useAttackHero(final ActionsInput action) {
        Coordinates cardAttackerCoords = action.getCardAttacker();

        int attackingPlayerIdx = getAttackingPlayer(cardAttackerCoords.getX());

        int xAttacker = cardAttackerCoords.getX();
        int yAttacker = cardAttackerCoords.getY();

        Card playerHero = players[attackingPlayerIdx].getPlayerHero();

        String command = "useAttackHero";

        Card cardAttacker = table.getTable().get(xAttacker).get(yAttacker);

        if (cardAttacker.getAttackStatus()) {
            ObjectNode attackErrorNode = createOneCardCoordsErrorNode(command, cardAttackerCoords);
            attackErrorNode.put("error", "Attacker card has already attacked this turn.");
            output.add(attackErrorNode);
        } else if (cardAttacker.getFrozenStatus()) {
            ObjectNode attackErrorNode = createOneCardCoordsErrorNode(command, cardAttackerCoords);
            attackErrorNode.put("error", "Attacker card is frozen.");
            output.add(attackErrorNode);
        } else if (isTankOnFrontRow(cardAttackerCoords.getX())) {
            ObjectNode attackErrorNode = createOneCardCoordsErrorNode(command, cardAttackerCoords);
            attackErrorNode.put("error", "Attacked card is not of type 'Tank'.");
            output.add(attackErrorNode);
        } else {
            cardAttacker.setHasAttacked(true);

            if (playerHero.getHealth() - cardAttacker.getAttackDamage() <= 0) {
                ObjectNode winNode = mapper.createObjectNode();
                if (attackingPlayerIdx == 0) {
                    winNode.put("gameEnded", "Player two killed the enemy hero.");
                    gameStatistics.setPlayerTwoWins(gameStatistics.getPlayerTwoWins() + 1);
                    output.add(winNode);
                } else {
                    winNode.put("gameEnded", "Player one killed the enemy hero.");
                    gameStatistics.setPlayerOneWins(gameStatistics.getPlayerOneWins() + 1);
                    output.add(winNode);
                }
            } else {
                playerHero.setHealth(playerHero.getHealth() - cardAttacker.getAttackDamage());
            }

       }

    }

    /**
     * Processes the "useHeroAbility" command,
     * applying the current player's hero ability to a specified row.
     * Validates mana, hero's attack status, and row ownership.
     *
     * @param action The action specifying the affected row.
     * @param player The current player using the hero ability.
     */
    private void useHeroAbility(final ActionsInput action, final Player player) {
        int affectedRow = action.getAffectedRow();
        String command = "useHeroAbility";


        if (player.getPlayerMana() < player.getPlayerHero().getMana()) {
            ObjectNode heroAbilityNode = mapper.createObjectNode();
            heroAbilityNode.put("command", command);
            heroAbilityNode.put("affectedRow", affectedRow);
            heroAbilityNode.put("error", "Not enough mana to use hero's ability.");
            output.add(heroAbilityNode);
        } else if (player.getPlayerHero().getAttackStatus()) {
            ObjectNode heroAbilityNode = mapper.createObjectNode();
            heroAbilityNode.put("command", command);
            heroAbilityNode.put("affectedRow", affectedRow);
            heroAbilityNode.put("error", "Hero has already attacked this turn.");
            output.add(heroAbilityNode);
        } else if (!checkAbilityCastConditions(player.getPlayerHero().getName(),
                                 currentPlayer - 1,
                                               affectedRow)) {
            ObjectNode heroAbilityNode = mapper.createObjectNode();
            heroAbilityNode.put("command", command);
            heroAbilityNode.put("affectedRow", affectedRow);
            if (player.getPlayerHero().getName().equals("Lord Royce")
                || player.getPlayerHero().getName().equals("Empress Thorina")) {
                heroAbilityNode.put("error",
                                 "Selected row does not belong to the enemy.");
            } else {
                heroAbilityNode.put("error",
                                 "Selected row does not belong to the current player.");
            }
            output.add(heroAbilityNode);
        } else {
            player.getPlayerHero().setHasAttacked(true);

            player.getPlayerHero().applyHeroAbility(table, affectedRow);

            for (int i = 0; i < table.getTable().get(affectedRow).size(); i++) {
                if (table.getTable().get(affectedRow).get(i).getHealth() == 0) {
                    table.getTable().get(affectedRow).remove(i);
                    break;
                }
            }

            player.setPlayerMana(player.getPlayerMana() - player.getPlayerHero().getMana());
        }
    }

    /**
     * Retrieves and outputs the mana of the specified player.
     *
     * @param action The action containing the player's index.
     */
    private void getPlayerMana(final ActionsInput action) {
        ObjectNode manaNode = mapper.createObjectNode();

        manaNode.put("command", "getPlayerMana");
        manaNode.put("playerIdx", action.getPlayerIdx());
        manaNode.put("output", players[action.getPlayerIdx() - 1].getPlayerMana());

        output.add(manaNode);
    }

    /**
     * Retrieves and outputs the deck of the specified player.
     *
     * @param action The action containing the player's index.
     */
    private void getPlayerDeck(final ActionsInput action) {
        ObjectNode deckNode = mapper.createObjectNode();

        deckNode.put("command", "getPlayerDeck");
        deckNode.put("playerIdx", action.getPlayerIdx());

        ArrayList<ObjectNode> cards = players[action.getPlayerIdx() - 1]
                                      .getPlayerDeck().createCardsArray(mapper);

        ArrayNode deckArray = mapper.createArrayNode();
        deckArray.addAll(cards);
        deckNode.set("output", deckArray);

        output.add(deckNode);
    }

    /**
     * Retrieves and outputs the cards currently in the hand of the specified player.
     *
     * @param action The action containing the player's index.
     */
    private void getPlayerHero(final ActionsInput action) {
        ObjectNode heroNode = mapper.createObjectNode();
        heroNode.put("command", "getPlayerHero");

        ObjectNode hero = players[action.getPlayerIdx() - 1].createHeroNode(mapper);

        heroNode.set("output", hero);
        heroNode.put("playerIdx", action.getPlayerIdx());

        output.add(heroNode);
    }

    /**
     * Retrieves and outputs the cards currently in the hand of the specified player.
     *
     * @param action The action containing the player's index.
     */
    private void getCardsInHand(final ActionsInput action) {
        ObjectNode handJob = mapper.createObjectNode();

        handJob.put("command", "getCardsInHand");
        handJob.put("playerIdx", action.getPlayerIdx());

        ArrayList<ObjectNode> cards = players[action.getPlayerIdx() - 1]
                                      .createCardsInHandArray(mapper);

        ArrayNode cardsInHandArray = mapper.createArrayNode();
        cardsInHandArray.addAll(cards);
        handJob.set("output", cardsInHandArray);

        output.add(handJob);
    }

    /**
     * Retrieves and outputs the cards currently on the table for all rows.
     */
    private void getCardsOnTable() {
        ObjectNode tableNode = mapper.createObjectNode();

        tableNode.put("command", "getCardsOnTable");

        ArrayNode tableArray = mapper.createArrayNode();
        ArrayNode rowArray0 = mapper.createArrayNode();
        ArrayNode rowArray1 = mapper.createArrayNode();
        ArrayNode rowArray2 = mapper.createArrayNode();
        ArrayNode rowArray3 = mapper.createArrayNode();

        for (Card card: players[1].getBackRow()) {
            ObjectNode cardNode = card.createCardNode(mapper);
            rowArray0.add(cardNode);
        }

        for (Card card: players[1].getFrontRow()) {
            ObjectNode cardNode = card.createCardNode(mapper);
            rowArray1.add(cardNode);
        }

        for (Card card: players[0].getFrontRow()) {
            ObjectNode cardNode = card.createCardNode(mapper);
            rowArray2.add(cardNode);
        }

        for (Card card: players[0].getBackRow()) {
            ObjectNode cardNode = card.createCardNode(mapper);
            rowArray3.add(cardNode);
        }

        tableArray.add(rowArray0);
        tableArray.add(rowArray1);
        tableArray.add(rowArray2);
        tableArray.add(rowArray3);

        tableNode.set("output", tableArray);

        output.add(tableNode);
    }

    /**
     * Retrieves and outputs the index of the player whose turn it currently is.
     */
    private void getPlayerTurn() {
        ObjectNode turnNode = mapper.createObjectNode();

        turnNode.put("command", "getPlayerTurn");
        turnNode.put("output", currentPlayer);

        output.add(turnNode);
    }


    /**
     * Retrieves and outputs the card at a specific position on the table.
     * If no card exists at the position, outputs an appropriate message.
     *
     * @param action The action specifying the coordinates (x, y) to check.
     */
    private void getCardAtPosition(final ActionsInput action) {
        int x = action.getX();
        int y = action.getY();

        if (table.getTable().get(x).size() <= y) {
            ObjectNode positionErrorNode = mapper.createObjectNode();

            positionErrorNode.put("command", "getCardAtPosition");
            positionErrorNode.put("x", x);
            positionErrorNode.put("y", y);
            positionErrorNode.put("output", "No card available at that position.");

            output.add(positionErrorNode);
        } else {
            ObjectNode positionNode = mapper.createObjectNode();

            positionNode.put("command", "getCardAtPosition");
            positionNode.put("x", x);
            positionNode.put("y", y);

            Card cardAtPosition = table.getTable().get(x).get(y);

            ObjectNode cardNode = cardAtPosition.createCardNode(mapper);

            positionNode.set("output", cardNode);

            output.add(positionNode);
        }
    }

    /**
     * Determines which player is the owner of a card on the table based on the row index.
     *
     * @param x The row index of the card.
     * @return The index of the attacking player (0 or 1).
     */
    private int getAttackingPlayer(final int x) {
        if (x < 2) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Retrieves and outputs all frozen cards currently on the table.
     */
    private void getFrozenCardsOnTable() {
        ObjectNode frozenNode = mapper.createObjectNode();
        frozenNode.put("command", "getFrozenCardsOnTable");

        ArrayNode frozenArray = mapper.createArrayNode();
        for (int i = 0; i < 4; i++) {
            for (Card card : table.getTable().get(i)) {
                if (card.getFrozenStatus()) {

                    ObjectNode cardNode = card.createCardNode(mapper);
                    frozenArray.add(cardNode);
                }
            }
        }

        frozenNode.set("output", frozenArray);
        output.add(frozenNode);
    }


    /**
     * Creates an error node
     * containing the command and coordinates of the attacker and attacked card.
     *
     * @param command The command being executed.
     * @param cardAttackerCoords Coordinates of the attacking card.
     * @param cardAttackedCoords Coordinates of the attacked card.
     * @return A JSON object containing error information.
     */
    private ObjectNode createCoordsErrorNode(final String command,
                                             final Coordinates cardAttackerCoords,
                                             final Coordinates cardAttackedCoords) {
        ObjectNode attackErrorNode = mapper.createObjectNode();
        attackErrorNode.put("command", command);

        ObjectNode attackerCoordsNode = mapper.createObjectNode();
        attackerCoordsNode.put("x", cardAttackerCoords.getX());
        attackerCoordsNode.put("y", cardAttackerCoords.getY());
        attackErrorNode.set("cardAttacker", attackerCoordsNode);

        ObjectNode attackedCoordsNode = mapper.createObjectNode();
        attackedCoordsNode.put("x", cardAttackedCoords.getX());
        attackedCoordsNode.put("y", cardAttackedCoords.getY());
        attackErrorNode.set("cardAttacked", attackedCoordsNode);

        return attackErrorNode;
    }

    /**
     * Creates an error node containing the command and coordinates of the attacking card.
     *
     * @param command The command being executed.
     * @param cardAttackerCoords Coordinates of the attacking card.
     * @return A JSON object containing error information.
     */
    private ObjectNode createOneCardCoordsErrorNode(final String command,
                                                    final Coordinates cardAttackerCoords) {
        ObjectNode attackErrorNode = mapper.createObjectNode();
        attackErrorNode.put("command", command);

        ObjectNode attackerCoordsNode = mapper.createObjectNode();
        attackerCoordsNode.put("x", cardAttackerCoords.getX());
        attackerCoordsNode.put("y", cardAttackerCoords.getY());
        attackErrorNode.set("cardAttacker", attackerCoordsNode);

        return attackErrorNode;
    }

    /**
     * Processes all actions in the game, delegating commands to the appropriate methods.
     * Tracks game statistics and handles game flow.
     */
    public void applyCommands() {
        gameStatistics.setTotalGamesPlayed(gameStatistics.getTotalGamesPlayed() + 1);
        startRound();
        for (ActionsInput action : actions) {
            switch (action.getCommand()) {
                case "getPlayerDeck":
                    getPlayerDeck(action);
                    break;
                case "getPlayerHero":
                    getPlayerHero(action);
                    break;
                case "endPlayerTurn":
                    endPlayerTurn(action);
                    break;
                case "getPlayerTurn":
                    getPlayerTurn();
                    break;
                case "placeCard":
                    placeCard(players[currentPlayer - 1], action.getHandIdx());
                    break;
                case "getCardsInHand":
                    getCardsInHand(action);
                    break;
                case "getPlayerMana":
                    getPlayerMana(action);
                    break;
                case "getCardsOnTable":
                    getCardsOnTable();
                    break;
                case "cardUsesAttack":
                    cardUsesAttack(action);
                    break;
                case "getCardAtPosition":
                    getCardAtPosition(action);
                    break;
                case "cardUsesAbility":
                    cardUsesAbility(action);
                    break;
                case "useAttackHero":
                    useAttackHero(action);
                    break;
                case "useHeroAbility":
                    useHeroAbility(action, players[currentPlayer - 1]);
                    break;
                case "getFrozenCardsOnTable":
                    getFrozenCardsOnTable();
                    break;
                case "getTotalGamesPlayed":
                    gameStatistics.printTotalGamesPlayed(mapper, output);
                    break;
                case "getPlayerOneWins":
                    gameStatistics.printPlayerOneWins(mapper, output);
                    break;
                case "getPlayerTwoWins":
                    gameStatistics.printPlayerTwoWins(mapper, output);
                    break;
                default:
                    System.out.println("Undefined command.");
            }
        }
    }
}
