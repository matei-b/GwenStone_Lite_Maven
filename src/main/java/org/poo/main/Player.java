package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private static final int PLAYER_HEALTH = 30;

    private final int playerIdx;
    private final Card playerHero;
    private final Deck playerDeck;
    private final ArrayList<Card> playerCardsInHand = new ArrayList<>();
    private int playerMana = 0;
    private int statusTurn = 0;
    private final ArrayList<Card> frontRow;
    private final ArrayList<Card> backRow;


    /***
     * Constructor for the Player object. Generated each new game.
     *
     * @param playerIdx int
     * @param deck Deck
     * @param playerHero CardInput
     * @param frontRow ArrayList<Card>
     * @param backRow ArrayList<Card>
     * @param shuffleSeed long
     */
    public Player(final int playerIdx, final Deck deck, final CardInput playerHero,
                  final ArrayList<Card> frontRow, final ArrayList<Card> backRow,
                  final long shuffleSeed) {
        this.playerIdx = playerIdx;
        playerDeck = new Deck(deck.getDeckCardInput(), shuffleSeed);
        this.playerHero = new Card(playerHero);
        this.playerHero.setHealth(PLAYER_HEALTH);
        this.frontRow = frontRow;
        this.backRow = backRow;
    }

    /***
     * Add to the players hand a card from their deck;
     */
    public void addToPlayerHand() {
        playerCardsInHand.add(playerDeck.getDeck().remove(0));
    }

    /***
     * Gettter
     */
    public ArrayList<Card> getPlayerCardsInHand() {
        return playerCardsInHand;
    }

    /***
     * Gettter
     */
    public Deck getPlayerDeck() {
        return playerDeck;
    }

    /***
     * Gettter
     */
    public int getPlayerIdx() {
        return playerIdx;
    }

    /***
     * Gettter
     */
    public ArrayList<Card> getFrontRow() {
        return frontRow;
    }

    /***
     * Gettter
     */
    public ArrayList<Card> getBackRow() {
        return backRow;
    }

    /***
     * Gettter
     */
    public Card getPlayerHero() {
        return playerHero;
    }

    /***
     * Gettter
     */
    public int getPlayerMana() {
        return playerMana;
    }

    /***
     * Gettter
     */
    public int getStatusTurn() {
        return statusTurn;
    }

    /***
     * Settter
     */
    public void setPlayerMana(final int playerMana) {
        this.playerMana = playerMana;
    }

    /***
     * Settter
     */
    public void setStatusTurn(final int statusTurn) {
        this.statusTurn = statusTurn;
    }

    /**
     * Creates an array of JSON objects representing the cards currently in the player's hand.
     *
     * @param mapper The Jackson ObjectMapper used to create JSON objects.
     * @return An ArrayList of ObjectNode representing the cards in the player's hand.
     */
    public ArrayList<ObjectNode> createCardsInHandArray(final ObjectMapper mapper) {
        ArrayList<ObjectNode> cards = new ArrayList<ObjectNode>();

        for (Card card: playerCardsInHand) {
            ObjectNode cardNode = mapper.createObjectNode();
            cardNode = card.createCardNode(mapper);
            cards.add(cardNode);
        }

        return cards;
    }

    /**
     * Creates a JSON object representing the player's hero card.
     *
     * @param mapper The Jackson ObjectMapper used to create the JSON object.
     * @return An ObjectNode representing the player's hero card.
     */
    public ObjectNode createHeroNode(final ObjectMapper mapper) {
        ObjectNode heroNode = mapper.createObjectNode();
        heroNode.put("mana", playerHero.getMana());
        heroNode.put("health", playerHero.getHealth());
        heroNode.put("description", playerHero.getDescription());

        ArrayNode colorsArray = mapper.createArrayNode();
        for (String color : playerHero.getColors()) {
            colorsArray.add(color);
        }
        heroNode.set("colors", colorsArray);

        heroNode.put("name", playerHero.getName());

        return heroNode;
    }
}
