package ru.nsu.zenin.cardgame;

public class Card {

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.toString() + " " + suit.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Card)) {
            return false;
        }

        return this.suit == ((Card) obj).getSuit() && this.rank == ((Card) obj).getRank();
    }

    public enum Suit {
        CLUBS("♣"),
        DIAMONDS("♦"),
        HEARTS("♥"),
        SPADES("♠");

        private final String asString;

        private Suit(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }
    }

    public enum Rank {
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("Jack"),
        QUEEN("Queen"),
        KING("King"),
        ACE("Ace"),
        JOKER("Joker");

        private final String asString;

        private Rank(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }
    }
}
