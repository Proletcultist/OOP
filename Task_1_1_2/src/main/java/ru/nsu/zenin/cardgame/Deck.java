package ru.nsu.zenin.cardgame;

import java.util.Collections;
import java.util.Stack;
import java.util.function.Function;

public final class Deck {
    private final Stack<Card> cards;

    public Deck() {
        this.cards = new Stack<Card>();
    }

    public Deck(Card... cards) {
        this.cards = new Stack<Card>();

        Collections.addAll(this.cards, cards);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card getTop() {
        return cards.pop();
    }

    public void putOnTop(Card card) {
        cards.push(card);
    }

    public <T extends Card> void convertAllCards(Function<Card, T> convert) {
        for (int i = 0; i < cards.size(); i++) {
            cards.set(i, (Card) convert.apply(cards.get(i)));
        }
    }
}
