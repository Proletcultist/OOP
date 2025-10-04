package ru.nsu.zenin.blackjack;

import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Deck;
import ru.nsu.zenin.cardgame.Game;
import ru.nsu.zenin.cardgame.Message;
import ru.nsu.zenin.cardgame.PlayerInterface;
import ru.nsu.zenin.cardgame.Request;
import ru.nsu.zenin.cardgame.Response;
import ru.nsu.zenin.cardgame.StandardDecks;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackDriverTest {

    @RepeatedTest(25)
    public void driverOneRoundTest() {
        Game game =
                Game.builder()
                        .deck(StandardDecks.fullFrenchSuitedDeck())
                        .playerInterfaces(new PlayerInterface[] {new DriverTesterBot()})
                        .driver(new BlackjackDriver())
                        .build();
        game.startNextRound();
    }

    @RepeatedTest(5)
    public void driverFiveRoundsTest() {
        Game game =
                Game.builder()
                        .deck(StandardDecks.fullFrenchSuitedDeck())
                        .playerInterfaces(new PlayerInterface[] {new DriverTesterBot()})
                        .driver(new BlackjackDriver())
                        .build();
        for (int i = 0; i < 5; i++) {
            game.startNextRound();
        }
    }

    @RepeatedTest(10)
    public void driverTestWithFourCards() {
        Game game =
                Game.builder()
                        .deck(
                                new Deck(
                                        new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                                        new Card(Card.Suit.HEARTS, Card.Rank.TEN),
                                        new Card(Card.Suit.HEARTS, Card.Rank.TWO),
                                        new Card(Card.Suit.HEARTS, Card.Rank.FIVE)))
                        .playerInterfaces(new PlayerInterface[] {new DriverTesterBot()})
                        .driver(new BlackjackDriver())
                        .build();
        game.startNextRound();
    }

    private static class DriverTesterBot implements PlayerInterface {

        private int lastPlayerCardsAmount, lastDealerCardsAmount;
        private final Random rand = new Random();

        public void tell(Message message) {
            if (!(message instanceof BlackjackMessage)) {
                throw new DriverException(
                        "DriverTesterBot received message which isn't BlackjackMessage");
            }

            BlackjackMessage msg = (BlackjackMessage) message;

            switch (msg.getType()) {
                case NO_CARDS_IN_DECK_LEFT:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on NO_CARDS_IN_DECK_LEFT message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on NO_CARDS_IN_DECK_LEFT message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on NO_CARDS_IN_DECK_LEFT message");

                    Assertions.assertEquals(
                            msg.getCardsInDeckLeft(),
                            0,
                            "There are cards in deck on NO_CARDS_IN_DECK_LEFT message");
                    break;
                case YOU_OPENED_CARD:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on YOU_OPENED_CARD message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            ++lastPlayerCardsAmount,
                            String.format(
                                    "Players card amount doesn't match expected value, excpected %d, actual %d on YOU_OPENED_CARD",
                                    lastPlayerCardsAmount, msg.getPlayerHand().size()));
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on YOU_OPENED_CARD message");

                    break;
                case DEALER_OPENED_CLOSED_CARD:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on DEALER_OPENED_CLOSED_CARD message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on DEALER_OPENED_CLOSED_CARD message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on DEALER_OPENED_CLOSED_CARD message");

                    Assertions.assertFalse(
                            msg.getDealerHand().hasHiddenCards(),
                            "Dealer has hidden cards on DEALER_OPENED_CLOSED_CARD message");

                    break;
                case DEALER_OPENED_NEW_CARD:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            ++lastDealerCardsAmount,
                            String.format(
                                    "Dealers card amount doesn't match expected value, excpected %d, actual %d on DEALER_OPENED_NEW_CARD",
                                    lastDealerCardsAmount, msg.getDealerHand().size()));
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on DEALER_OPENED_NEW_CARD message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on DEALER_OPENED_NEW_CARD message");

                    break;
                case DRAW:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on DRAW message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on DRAW message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on DRAW message");

                    Assertions.assertTrue(
                            msg.isDealerEndedHisTurn()
                                    && msg.getDealerHand().getPoints()
                                            == msg.getPlayerHand().getPoints(),
                            "Draw condition isn't true on DRAW message");

                    break;
                case DEALER_WON:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on DEALER_WON message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on DEALER_WON message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on DEALER_WON message");

                    if (msg.isDealerEndedHisTurn()) {
                        Assertions.assertTrue(
                                msg.getDealerHand().getPoints() > msg.getPlayerHand().getPoints(),
                                "Dealer win condition isn't true on DEALER_WON message");
                    } else {
                        Assertions.assertTrue(
                                msg.getDealerHand().getPoints() <= 21
                                        && (msg.getDealerHand().getPoints() == 21
                                                || msg.getPlayerHand().getPoints() > 21),
                                "Dealer win condition isn't true on DEALER_WON message");
                    }

                    break;
                case PLAYER_WON:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on PLAYER_WON message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on PLAYER_WON message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on PLAYER_WON message");

                    if (msg.isDealerEndedHisTurn()) {
                        Assertions.assertTrue(
                                msg.getPlayerHand().getPoints() > msg.getDealerHand().getPoints(),
                                "Player win condition isn't true on PLAYER_WON message");
                    } else {
                        Assertions.assertTrue(
                                msg.getPlayerHand().getPoints() <= 21
                                        && (msg.getPlayerHand().getPoints() == 21
                                                || msg.getDealerHand().getPoints() > 21),
                                "Player win condition isn't true on PLAYER_WON message");
                    }

                    break;
                case NEW_ROUND_WELCOME:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            2,
                            String.format(
                                    "Dealers card amount doesn't match expected value, excpected 2, actual %d on NEW_ROUND_WELCOME, message",
                                    lastDealerCardsAmount));
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            2,
                            String.format(
                                    "Players card amount doesn't match expected value, excpected 2, actual %d on NEW_ROUND_WELCOME message",
                                    lastPlayerCardsAmount));
                    Assertions.assertTrue(
                            msg.getDealerHand().getPoints() == 21
                                    || msg.getPlayerHand().getPoints() == 21
                                    || msg.getDealerHand().hasHiddenCards(),
                            "Dealer doesn't have hidden card on NEW_ROUND_WELCOME message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on NEW_ROUND_WELCOME message");
                    Assertions.assertFalse(
                            msg.isDealerEndedHisTurn(),
                            "Dealer ended his turn on NEW_ROUND_WELCOME message");
                    Assertions.assertTrue(
                            msg.getDealerHand().getPoints() <= 21,
                            "Dealer has > 21 point on NEW_ROUND_WELCOME message");
                    Assertions.assertTrue(
                            msg.getPlayerHand().getPoints() <= 21,
                            "Player has > 21 point on NEW_ROUND_WELCOME message");

                    lastDealerCardsAmount = 2;
                    lastPlayerCardsAmount = 2;
                    break;
                case WELCOME:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            0,
                            String.format(
                                    "Dealers card amount doesn't match expected value, excpected 0, actual %d on WELCOME message",
                                    lastDealerCardsAmount));
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            0,
                            String.format(
                                    "Players card amount doesn't match expected value, excpected 0, actual %d on WELCOME message",
                                    lastPlayerCardsAmount));
                    Assertions.assertEquals(
                            msg.getDealerHand().getPoints(),
                            0,
                            "Dealer has non-zero points on WELCOME message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().getPoints(),
                            0,
                            "Player has non-zero points on WELCOME message");
                    Assertions.assertFalse(
                            msg.isDealerEndedHisTurn(), "Dealer ended his turn on WELCOME message");
                    break;
                case HANDS_INFO:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on HANDS_INFO message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on HANDS_INFO message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on HANDS_INFO message");
                    break;
                case TURN_CHANGE:
                    Assertions.assertEquals(
                            msg.getDealerHand().size(),
                            lastDealerCardsAmount,
                            "Dealers cards amount changed with no reason on TURN_CHANGE message");
                    Assertions.assertEquals(
                            msg.getPlayerHand().size(),
                            lastPlayerCardsAmount,
                            "Players cards amount changed with no reason on TURN_CHANGE message");
                    Assertions.assertFalse(
                            msg.getPlayerHand().hasHiddenCards(),
                            "Player has hidden cards on TURN_CHANGE message");
                    break;
            }
        }

        public Response ask(Request req) {
            if (!(req instanceof BlackjackRequest)) {
                throw new DriverException(
                        "DriverTesterBot received request which isn't BlackjackRequest");
            }

            if (rand.nextInt(2) == 1) {
                return new BlackjackResponse("", true);
            } else {
                return new BlackjackResponse("", false);
            }
        }

        public void printLinesSeparator() {}
    }
}
