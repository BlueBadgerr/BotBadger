package org.bluebadger.libraries;

public class Card {
    private Rank rank;
    private Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public String toString() {
        return rank.getLabel() + "-" + suit.name();
    }

    public enum Rank {
        ACE ("A"),
        TWO ("2"),
        THREE ("3"),
        FOUR ("4"),
        FIVE ("5"),
        SIX ("6"),
        SEVEN ("7"),
        EIGHT ("8"),
        NINE ("9"),
        TEN ("10"),
        JACK ("J"),
        QUEEN ("Q"),
        KING ("K");

        private String label;

        Rank(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Suit {
        SPADES, HEARTS, CLUBS, DIAMONDS
    }
}
