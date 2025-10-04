package ru.nsu.zenin.cardgame;

import java.util.Locale;
import java.util.ResourceBundle;

public class Card {

    private static final ResourceBundle cardRanks =
            ResourceBundle.getBundle("cardgame.CardRanks", Locale.getDefault());

    private static final ResourceBundle cardSuits =
            ResourceBundle.getBundle("cardgame.CardSuits", Locale.getDefault());

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
        CLUBS(cardSuits.getString("clubs")),
        DIAMONDS(cardSuits.getString("diamonds")),
        HEARTS(cardSuits.getString("hearts")),
        SPADES(cardSuits.getString("spades"));

        private final String asString;

        private Suit(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }
    }

    public enum Rank {
        TWO(cardRanks.getString("two")),
        THREE(cardRanks.getString("three")),
        FOUR(cardRanks.getString("four")),
        FIVE(cardRanks.getString("five")),
        SIX(cardRanks.getString("six")),
        SEVEN(cardRanks.getString("seven")),
        EIGHT(cardRanks.getString("eight")),
        NINE(cardRanks.getString("nine")),
        TEN(cardRanks.getString("ten")),
        JACK(cardRanks.getString("jack")),
        QUEEN(cardRanks.getString("queen")),
        KING(cardRanks.getString("king")),
        ACE(cardRanks.getString("ace")),
        JOKER(cardRanks.getString("joker"));

        private final String asString;

        private Rank(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }
    }
}
