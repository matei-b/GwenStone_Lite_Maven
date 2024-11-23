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

    public Deck(ArrayList<CardInput> deckCardInput, long shuffleSeed) {
        this.deckCardInput = new ArrayList<>(deckCardInput);
        Random rnd = new Random(shuffleSeed);
        for (CardInput card : deckCardInput)
            deck.add(new Card(card));
        shuffle(deck, rnd);
    }

    public ArrayList<CardInput> getDeckCardInput() {
        return deckCardInput;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public ArrayList<ObjectNode> createCardsArray(ObjectMapper mapper) {
        ArrayList<ObjectNode> cards = new ArrayList<ObjectNode>();

        for (Card card: deck) {
            ObjectNode cardNode = mapper.createObjectNode();
            cardNode = card.createCardNode(mapper);
            cards.add(cardNode);
        }

        return cards;
    }

}
