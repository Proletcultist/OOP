package ru.nsu.zenin.CLIblackjack;

import ru.nsu.zenin.cardgames.*;

public class App {
    public static void main(String[] args) {
        CardDeck deck = StandardCardDecks.fullFrenchSuitedDeck();

	deck.shuffle();

        while (!deck.isEmpty()) {
            System.out.println(deck.getTop().toString());
        }
    }
}
