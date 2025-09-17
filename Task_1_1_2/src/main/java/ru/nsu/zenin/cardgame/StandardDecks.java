package ru.nsu.zenin.cardgame;

import static ru.nsu.zenin.cardgame.Card.Rank.*;
import static ru.nsu.zenin.cardgame.Card.Suit.*;

public final class StandardDecks {

    private StandardDecks() {}

    public static Deck fullFrenchSuitedDeck() {
        return new Deck(
                new Card(CLUBS, TWO),
                new Card(CLUBS, THREE),
                new Card(CLUBS, FOUR),
                new Card(CLUBS, FIVE),
                new Card(CLUBS, SIX),
                new Card(CLUBS, SEVEN),
                new Card(CLUBS, EIGHT),
                new Card(CLUBS, NINE),
                new Card(CLUBS, TEN),
                new Card(CLUBS, JACK),
                new Card(CLUBS, QUEEN),
                new Card(CLUBS, KING),
                new Card(CLUBS, ACE),
                new Card(DIAMONDS, TWO),
                new Card(DIAMONDS, THREE),
                new Card(DIAMONDS, FOUR),
                new Card(DIAMONDS, FIVE),
                new Card(DIAMONDS, SIX),
                new Card(DIAMONDS, SEVEN),
                new Card(DIAMONDS, EIGHT),
                new Card(DIAMONDS, NINE),
                new Card(DIAMONDS, TEN),
                new Card(DIAMONDS, JACK),
                new Card(DIAMONDS, QUEEN),
                new Card(DIAMONDS, KING),
                new Card(DIAMONDS, ACE),
                new Card(HEARTS, TWO),
                new Card(HEARTS, THREE),
                new Card(HEARTS, FOUR),
                new Card(HEARTS, FIVE),
                new Card(HEARTS, SIX),
                new Card(HEARTS, SEVEN),
                new Card(HEARTS, EIGHT),
                new Card(HEARTS, NINE),
                new Card(HEARTS, TEN),
                new Card(HEARTS, JACK),
                new Card(HEARTS, QUEEN),
                new Card(HEARTS, KING),
                new Card(HEARTS, ACE),
                new Card(SPADES, TWO),
                new Card(SPADES, THREE),
                new Card(SPADES, FOUR),
                new Card(SPADES, FIVE),
                new Card(SPADES, SIX),
                new Card(SPADES, SEVEN),
                new Card(SPADES, EIGHT),
                new Card(SPADES, NINE),
                new Card(SPADES, TEN),
                new Card(SPADES, JACK),
                new Card(SPADES, QUEEN),
                new Card(SPADES, KING),
                new Card(SPADES, ACE));
    }

    // Other standatd card decks, like 36-card stripped French-Suited deck
}
