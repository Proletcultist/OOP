package ru.nsu.zenin.cardgame;

import java.util.LinkedList;
import java.util.stream.Stream;

public class Hand {

    private final LinkedList<Card> cards = new LinkedList<Card>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCards(Card... cards) {
        for (Card card : cards) {
            this.addCard(card);
        }
    }

    public int size() {
        return cards.size();
    }

    public Card getCard(int index) {
        try {
            return cards.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(
                    String.format(
                            "Index %d is out of bound. Actual hand size is %d",
                            index, cards.size()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unexpected exception occured when tried to add Card to hand");
        }
    }

    public Card removeCard(int index) {
        try {
            return cards.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(
                    String.format(
                            "Index %d is out of bound. Actual hand size is %d",
                            index, cards.size()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unexpected exception occured when tried to add Card to hand");
        }
    }

    public Stream<Card> stream() {
        return cards.stream();
    }

    @Override
    public String toString() {
        return "["
                + stream().map(card -> card.toString()).reduce((a, b) -> a + ", " + b).orElse("")
                + "]";
    }
}
