package org.bluebadger.pontoon;

import org.bluebadger.libraries.Card;

public class Player {
    private final String userId;
    private long gold;
    private Card[] cards = new Card[2];

    public Player(String userId, long gold) {
        this.userId = userId;
        this.gold = gold;
    }
}
