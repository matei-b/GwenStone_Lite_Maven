package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;

import java.util.ArrayList;
import java.util.Random;

import static java.util.Collections.shuffle;

public class Deck {
    private ArrayList<CardInput> deckCardInput = new ArrayList<CardInput>();
    private ArrayList<Card> deck = new ArrayList<Card>();

    /***
     * Constructor for Deck object.
     *
     * @param deckCardInput ArrayList<CardInput>
     * @param shuffleSeed long
     */
    public Deck(final ArrayList<CardInput> deckCardInput,
                final long shuffleSeed) {
        this.deckCardInput = new ArrayList<>(deckCardInput);
        Random rnd = new Random(shuffleSeed);
        for (CardInput card : deckCardInput) {
            deck.add(new Card(card));
        }
        shuffle(deck, rnd);
    }

    /***
     * Getter
     */
    public ArrayList<CardInput> getDeckCardInput() {
        return deckCardInput;
    }

    /***
     * Getter
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }

    /**
     * Creates an ArrayList of JSON representations of the cards in the deck.
     * Each card is converted into an ObjectNode using the provided ObjectMapper.
     *
     * @param mapper The Jackson ObjectMapper used to create JSON objects.
     * @return An ArrayList of ObjectNode representing the cards in the deck.
     */
    public ArrayList<ObjectNode> createCardsArray(final ObjectMapper mapper) {
        ArrayList<ObjectNode> cards = new ArrayList<ObjectNode>();

        for (Card card: deck) {
            ObjectNode cardNode = mapper.createObjectNode();
            cardNode = card.createCardNode(mapper);
            cards.add(cardNode);
        }

        return cards;
    }

}
