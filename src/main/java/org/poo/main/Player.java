package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private int playerIdx;
    //modifica contorizarea de jocuri noi;
    private int gamesWon = 0;
    private Card playerHero;
    private Deck playerDeck;
    private ArrayList<Card> playerCardsInHand = new ArrayList<>();
    private int playerMana = 0;
    private int statusTurn = 0;
    private ArrayList<Card> frontRow;
    private ArrayList<Card> backRow;

    public Player(int playerIdx, Deck deck, CardInput playerHero,
                  ArrayList<Card> frontRow, ArrayList<Card>backRow,
                  long shuffleSeed) {
        this.playerIdx = playerIdx;
        playerDeck = new Deck(deck.getDeckCardInput(), shuffleSeed);
        this.playerHero = new Card(playerHero);
        this.playerHero.setHealth(30);
        this.frontRow = frontRow;
        this.backRow = backRow;
    }

    public void playerHand() {
        playerCardsInHand.add(playerDeck.getDeck().remove(0));
    }

    public ArrayList<Card> getPlayerCardsInHand() {
        return playerCardsInHand;
    }

    public Deck getPlayerDeck() {
        return playerDeck;
    }

    public int getPlayerIdx() {
        return playerIdx;
    }

    public ArrayList<Card> getFrontRow() {
        return frontRow;
    }

    public ArrayList<Card> getBackRow() {
        return backRow;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public Card getPlayerHero() {
        return playerHero;
    }

    public int getPlayerMana() {
        return playerMana;
    }

    public int getStatusTurn() {
        return statusTurn;
    }

    public void setPlayerMana(int playerMana) {
        this.playerMana = playerMana;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setStatusTurn(int statusTurn) {
        this.statusTurn = statusTurn;
    }

    public ArrayList<ObjectNode> createCardsInHandArray(ObjectMapper mapper) {
        ArrayList<ObjectNode> cards = new ArrayList<ObjectNode>();

        for (Card card: playerCardsInHand) {
            ObjectNode cardNode = mapper.createObjectNode();
            cardNode = card.createCardNode(mapper);
            cards.add(cardNode);
        }

        return cards;
    }

    public ObjectNode createHeroNode(ObjectMapper mapper) {
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
