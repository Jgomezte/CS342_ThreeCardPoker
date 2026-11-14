package edu.uic.cs342.poker.model;

import java.util.ArrayList;

public class Dealer {
    private final Deck deck;

    public Dealer() {
        deck = new Deck();
        deck.shuffle();
    }

    public ArrayList<Card> dealHand() {
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hand.add(deck.deal());
        }
        return hand;
    }

    public void shuffleDeck() {
        deck.reset();
        deck.shuffle();
    }
}
