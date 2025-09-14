package ru.nsu.zenin.cardgames;

import java.util.Collections;
import java.util.Stack;

public final class CardDeck {
    private Stack<Card> cards;

    public CardDeck() {
        this.cards = new Stack<Card>();
    }

    public CardDeck(Card... cards) {
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
}
