package ru.nsu.zenin.cardgames;

public final class Card {

    private final CardSuit suit;
    private final CardRank rank;

    public Card(CardSuit suit, CardRank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public CardRank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.toString() + " " + suit.toString();
    }

    public enum CardSuit {
        CLUBS("♣"),
        DIAMONDS("♦"),
        HEARTS("♥"),
        SPADES("♠");

        private final String asString;

        private CardSuit(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }
    }

    public enum CardRank {
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

        private CardRank(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }
    }
}
