package org.bluebadger.pontoon;

import org.bluebadger.libraries.Card;
import org.bluebadger.libraries.Deck;

import java.util.*;

public class Shuffler {
    private final Random random = new Random();

    List<Card> cards = new ArrayList<>();
    Iterator<Card> iterator;
    int shuffleLatch;
    boolean shuffleAtRoundEnd;

    public Shuffler() {
        // Do 6 decks for now
        for (int i = 0; i < 6; i++) {
            cards.addAll(Deck.pontoonDeck());
        }

        shuffle();
        iterator = cards.iterator();
        shuffleAtRoundEnd = false;
    }

    public Card draw() {
        shuffleLatch--;

        if (shuffleLatch == 0) {
            shuffleAtRoundEnd = true;
        }

        return iterator.next();
    }

    public void endRound() {
        if (shuffleAtRoundEnd) {
            shuffle();
            shuffleAtRoundEnd = false;
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);

        // Add a blank card to trigger shuffle. This card will be placed between 60 and 75 cards from the end
        shuffleLatch = cards.size() - (random.nextInt(16) + 60);
    }
}
