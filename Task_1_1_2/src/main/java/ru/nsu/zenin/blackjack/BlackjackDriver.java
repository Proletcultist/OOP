package ru.nsu.zenin.blackjack;

import java.util.Locale;
import java.util.ResourceBundle;
import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Card.Rank.*;
import ru.nsu.zenin.cardgame.Deck;
import ru.nsu.zenin.cardgame.Driver;
import ru.nsu.zenin.cardgame.Game;
import ru.nsu.zenin.cardgame.PlayerInterface;
import ru.nsu.zenin.cardgame.Response;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackDriver implements Driver {

    private static final ResourceBundle resources =
            ResourceBundle.getBundle("blackjack.Messages", Locale.getDefault());

    private boolean isPlayersTurn = true;
    private boolean isDealerEndedHisTurn = false;
    private int roundNumber = 1;
    private int playerWins = 0, dealerWins = 0;

    private final BlackjackPlayer player = new BlackjackPlayer();
    private final BlackjackPlayer dealer = new BlackjackPlayer();
    private Game game;
    private Deck deck;
    private HandsStatus handsStatus = HandsStatus.UNFINISHED;

    private PlayerInterface playerInterface;

    public void initializeGame(Game game) {
        if (game.getPlayerInterfaces().length != 1) {
            throw new DriverException(
                    String.format(
                            resources.getString("needExactlyOnePlayerError"),
                            game.getPlayerInterfaces().length));
        }
        if (game.getDeck().size() < 4) {
            throw new DriverException(
                    String.format(
                            resources.getString("needAtLeastFourCards"), game.getDeck().size()));
        }

        this.game = game;
        playerInterface = game.getPlayerInterfaces()[0];
        deck = game.getDeck();

        deck.convertAllCards(card -> new BlackjackCard(card));

        sendMessage(BlackjackMessage.MessageType.WELCOME, resources.getString("newGameWelcome"));

        startRound();
    }

    public void step() {
        handsStatus = checkHands();
        if (handsStatus == HandsStatus.UNFINISHED) {
            if (isPlayersTurn) {
                playersTurn();
            } else {
                dealersTurn();
            }
        } else {
            endRound();
        }
    }

    public void initializeNextRound() {
        while (player.getHand().size() != 0) {
            deck.putOnTop(player.getHand().removeCard(0));
        }
        while (dealer.getHand().size() != 0) {
            deck.putOnTop(dealer.getHand().removeCard(0));
        }
        isPlayersTurn = true;
        isDealerEndedHisTurn = false;

        roundNumber++;

        playerInterface.printLinesSeparator();

        startRound();
    }

    private void startRound() {
        deck.shuffle();

        dealer.getHand().addCards(deck.getTop(), deck.getTop());

        player.getHand().addCards(deck.getTop(), deck.getTop());

        handsStatus = checkHands();
        if (handsStatus == HandsStatus.UNFINISHED) {
            dealer.getHand().hideCard(1);
        }
        printRoundStartMessage();

        playerInterface.printLinesSeparator();

        if (handsStatus == HandsStatus.UNFINISHED) {
            isPlayersTurn = false;
            changeTurn();
        }
    }

    private void playersTurn() {

        if (deck.isEmpty()) {
            sendMessage(
                    BlackjackMessage.MessageType.NO_CARDS_IN_DECK_LEFT,
                    resources.getString("noCardsInDeckLeft"));
            printHands();
            playerInterface.printLinesSeparator();

            changeTurn();
            return;
        }

        String choice;
        while (true) {
            Response resp = askPlayer(resources.getString("askPlayerMessage"));
            if (resp instanceof BlackjackResponse) {
                if (((BlackjackResponse) resp).getTakeCard()) {
                    choice = "1";
                } else {
                    choice = "0";
                }
            } else {
                choice = resp.getText();
            }

            if (choice.equals("1") || choice.equals("0")) {
                break;
            }
        }

        if (choice.equals("1")) {
            Card takenCard = deck.getTop();

            player.getHand().addCard(takenCard);
            sendMessage(
                    BlackjackMessage.MessageType.YOU_OPENED_CARD,
                    String.format(resources.getString("youOpenedCard"), takenCard.toString()));

            printHands();
            playerInterface.printLinesSeparator();
        } else {
            playerInterface.printLinesSeparator();
            changeTurn();
        }
    }

    private void dealersTurn() {

        if (dealer.getHand().hasHiddenCards()) {
            dealer.getHand().unhideCard(1);

            sendMessage(
                    BlackjackMessage.MessageType.DEALER_OPENED_CLOSED_CARD,
                    String.format(
                            resources.getString("dealerOpenedClosedCard"),
                            dealer.getHand().getCard(1).toString()));
            printHands();
            playerInterface.printLinesSeparator();
        }

        if (deck.isEmpty()) {
            sendMessage(
                    BlackjackMessage.MessageType.NO_CARDS_IN_DECK_LEFT,
                    resources.getString("noCardsInDeckLeft"));
            printHands();
            playerInterface.printLinesSeparator();

            isDealerEndedHisTurn = true;
            return;
        }

        if (dealer.getHand().getPoints() < 17) {
            Card takenCard = deck.getTop();

            dealer.getHand().addCard(takenCard);
            sendMessage(
                    BlackjackMessage.MessageType.DEALER_OPENED_NEW_CARD,
                    String.format(
                            resources.getString("dealerOpenedNewCard"), takenCard.toString()));

            printHands();
            playerInterface.printLinesSeparator();
        } else {
            isDealerEndedHisTurn = true;
        }
    }

    private void changeTurn() {
        if (isPlayersTurn) {
            sendMessage(
                    BlackjackMessage.MessageType.TURN_CHANGE,
                    resources.getString("dealersTurn") + "\n-------");
        } else {
            sendMessage(
                    BlackjackMessage.MessageType.TURN_CHANGE,
                    resources.getString("yourTurn") + "\n-------");
        }

        isPlayersTurn = !isPlayersTurn;
    }

    private HandsStatus checkHands() {
        if (isDealerEndedHisTurn) {
            return endGameCheckHands();
        }

        return gameRunningCheckHands();
    }

    private HandsStatus endGameCheckHands() {
        int comparisonRes =
                Integer.signum(
                        Integer.compare(
                                player.getHand().getPoints(), dealer.getHand().getPoints()));
        return switch (comparisonRes) {
            case 0 -> HandsStatus.DRAW;
            case 1 -> HandsStatus.PLAYER_WIN;
            case -1 -> HandsStatus.DEALER_WIN;
            default ->
                    throw new DriverException(
                            String.format(
                                    resources.getString("unexpectedReturnFrom"),
                                    "Integer.signum",
                                    String.valueOf(comparisonRes)));
        };
    }

    private HandsStatus gameRunningCheckHands() {
        if (player.getHand().getPoints() > 21) {
            return HandsStatus.DEALER_WIN;
        }
        if (dealer.getHand().getPoints() > 21) {
            return HandsStatus.PLAYER_WIN;
        }
        if (player.getHand().getPoints() == 21) {
            return HandsStatus.PLAYER_WIN;
        }
        if (dealer.getHand().getPoints() == 21) {
            return HandsStatus.DEALER_WIN;
        }

        return HandsStatus.UNFINISHED;
    }

    private void printRoundStartMessage() {
        sendMessage(
                BlackjackMessage.MessageType.NEW_ROUND_WELCOME,
                String.format(resources.getString("roundAnnouncement"), roundNumber)
                        + "\n"
                        + resources.getString("dealerDealtTheCards"));

        printHands();
    }

    private void printHands() {
        sendMessage(
                BlackjackMessage.MessageType.HANDS_INFO,
                "\t"
                        + String.format(
                                resources.getString("yourHand"), player.getHand().toString())
                        + "\n\t"
                        + String.format(
                                resources.getString("dealersHand"), dealer.getHand().toString()));
    }

    private void endRound() {
        game.setStatus(Game.GameStatus.DONE);

        BlackjackMessage.MessageType type;

        switch (handsStatus) {
            case DEALER_WIN:
                dealerWins++;
                type = BlackjackMessage.MessageType.DEALER_WON;
                break;
            case PLAYER_WIN:
                playerWins++;
                type = BlackjackMessage.MessageType.PLAYER_WON;
                break;
            case DRAW:
                type = BlackjackMessage.MessageType.DRAW;
                break;
            default:
                throw new DriverException(resources.getString("driverUnexpectedlyTriedToEndRount"));
        }
        sendMessage(type, getRoundEndingString());
    }

    private String getRoundEndingString() {
        int winsComparisonRes = Integer.signum(Integer.compare(playerWins, dealerWins));
        return switch (handsStatus) {
                    case DRAW -> resources.getString("drawMessage");
                    case PLAYER_WIN -> resources.getString("playerWin");
                    case DEALER_WIN -> resources.getString("dealerWin");
                    default ->
                            new DriverException(
                                    resources.getString("driverUnexpectedlyTriedToEndRount"));
                }
                + " "
                + String.format(resources.getString("scoreMessage"), playerWins, dealerWins)
                + " "
                + switch (winsComparisonRes) {
                    case 0 -> resources.getString("roundsDraw");
                    case 1 -> resources.getString("roundsPlayerWin");
                    case -1 -> resources.getString("roundsDealerWin");
                    default ->
                            throw new DriverException(
                                    String.format(
                                            resources.getString("unexpectedReturnFrom"),
                                            "Integer.signum",
                                            winsComparisonRes));
                };
    }

    private void sendMessage(BlackjackMessage.MessageType type, String msg) {
        playerInterface.tell(
                BlackjackMessage.builder()
                        .type(type)
                        .dealerHand(dealer.getHand())
                        .playerHand(player.getHand())
                        .cardsInDeckLeft(deck.size())
                        .isDealerEndedHisTurn(isDealerEndedHisTurn)
                        .msg(msg)
                        .build());
    }

    private Response askPlayer(String msg) {
        return playerInterface.ask(
                BlackjackRequest.builder()
                        .dealerHand(dealer.getHand())
                        .playerHand(player.getHand())
                        .msg(msg)
                        .build());
    }

    private enum HandsStatus {
        UNFINISHED,
        DEALER_WIN,
        PLAYER_WIN,
        DRAW
    }
}
