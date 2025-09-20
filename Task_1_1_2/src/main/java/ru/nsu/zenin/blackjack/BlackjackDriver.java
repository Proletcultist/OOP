package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Card.Rank.*;
import ru.nsu.zenin.cardgame.Deck;
import ru.nsu.zenin.cardgame.Driver;
import ru.nsu.zenin.cardgame.Game;
import ru.nsu.zenin.cardgame.PlayerInterface;
import ru.nsu.zenin.cardgame.Response;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackDriver implements Driver {

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
                            "Need exactly 1 player for blackjack, got %d",
                            game.getPlayerInterfaces().length));
        }
        if (game.getDeck().size() < 4) {
            throw new DriverException(
                    String.format("Need at least 4 cards in deck, got %d", game.getDeck().size()));
        }

        this.game = game;
        playerInterface = game.getPlayerInterfaces()[0];
        deck = game.getDeck();

        deck.convertAllCards(card -> new BlackjackCard(card));

        sendMessage(BlackjackMessage.MessageType.WELCOME, "Welcome to the CLI blackjack");

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
                    BlackjackMessage.MessageType.NO_CARDS_IN_DECK_LEFT, "No cards in deck left");
            printHands();
            playerInterface.printLinesSeparator();

            changeTurn();
            return;
        }

        String choice;
        while (true) {
            Response resp = askPlayer("Enter \"1\" to take card and \"0\" to stop...");
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
                    "You opened card " + takenCard.toString());

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
                    "Dealer opened closed card " + dealer.getHand().getCard(1).toString());
            printHands();
            playerInterface.printLinesSeparator();
        }

        if (deck.isEmpty()) {
            sendMessage(
                    BlackjackMessage.MessageType.NO_CARDS_IN_DECK_LEFT, "No cards in deck left");
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
                    "Dealer opened card " + takenCard.toString());

            printHands();
            playerInterface.printLinesSeparator();
        } else {
            isDealerEndedHisTurn = true;
        }
    }

    private void changeTurn() {
        if (isPlayersTurn) {
            sendMessage(BlackjackMessage.MessageType.TURN_CHANGE, "Dealers' turn\n-------");
        } else {
            sendMessage(BlackjackMessage.MessageType.TURN_CHANGE, "Your turn\n-------");
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
                                    "Unexpected return from Integer.signum: %d", comparisonRes));
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
                String.format("Round %d", roundNumber) + "\nThe dealer dealt the cards");

        printHands();
    }

    private void printHands() {
        sendMessage(
                BlackjackMessage.MessageType.HANDS_INFO,
                "\tYour cards: "
                        + player.getHand().toString()
                        + "\n\tDealers' cards: "
                        + dealer.getHand().toString());
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
                throw new DriverException("Driver unexcpectedly tried to end round");
        }
        sendMessage(type, getRoundEndingString());
    }

    private String getRoundEndingString() {
        int winsComparisonRes = Integer.signum(Integer.compare(playerWins, dealerWins));
        return switch (handsStatus) {
                    case DRAW -> "It's a draw!";
                    case PLAYER_WIN -> "You won this round!";
                    case DEALER_WIN -> "Dealer won this round!";
                    default -> new DriverException("Driver unexcpectedly tried to end round");
                }
                + String.format(" Score is: %d:%d ", playerWins, dealerWins)
                + switch (winsComparisonRes) {
                    case 0 -> "Draw for now";
                    case 1 -> "You're winning now!";
                    case -1 -> "Dealer's winning now!";
                    default ->
                            throw new DriverException(
                                    String.format(
                                            "Unexpected return from Integer.signum: %d",
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
