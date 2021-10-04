package org.bluebadger.libraries;

import java.util.ArrayList;
import java.util.List;

import static org.bluebadger.libraries.Card.*;

public class Deck {

    public static List<Card> pontoonDeck() {
        ArrayList<Card> deck = new ArrayList<>();

        for (Rank rank : Rank.values()) {
            // Skip rank 10 for Pontoon
            if (rank != Rank.TEN) {
                for (Suit suit : Suit.values()) {
                    deck.add(new Card(rank, suit));
                }
            }
        }

        return deck;
    }
}
