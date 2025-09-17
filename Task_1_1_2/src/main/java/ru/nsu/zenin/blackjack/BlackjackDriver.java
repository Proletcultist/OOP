package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Card.Rank.*;
import ru.nsu.zenin.cardgame.Deck;
import ru.nsu.zenin.cardgame.Driver;
import ru.nsu.zenin.cardgame.Game;
import ru.nsu.zenin.cardgame.Hand;
import ru.nsu.zenin.cardgame.Message;
import ru.nsu.zenin.cardgame.PlayerInterface;
import ru.nsu.zenin.cardgame.Request;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackDriver implements Driver {

    private int playerPoints = 0, dealerPoints = 0;
    private boolean isPlayersTurn = true;
    private boolean isDealersCardsUnhidden = false;
    private boolean isDealerEndedHisTurn = false;
    private int roundNumber = 1;
    private int playerWins = 0, dealerWins = 0;

    private Game game;
    private Hand dealerHand = new Hand();
    private PlayerInterface playerInterface;
    private Deck deck;
    private HandsStatus handsStatus = HandsStatus.UNFINISHED;

    public void initializeGame(Game game) {
        if (game.getPlayerInterfaces().length != 1) {
            throw new DriverException(
                    String.format(
                            "Need exactly 1 player for blackjack, got %d",
                            game.getPlayers().length));
        }
        if (game.getDeck().size() < 4) {
            throw new DriverException(
                    String.format("Need at least 4 cards in deck, got %d", game.getDeck().size()));
        }

        this.game = game;
        playerInterface = game.getPlayerInterfaces()[0];
        deck = game.getDeck();

        deck.convertAllCards(card -> new BlackjackCard(card, false));

        playerInterface.tell(new Message("Welcome to the CLI Blackjack!"));

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

    public void initializeNextRound() {}

    private void startRound() {
        deck.shuffle();

        dealerHand.addCards(deck.getTop(), deck.getTop());
        dealerPoints +=
                dealerHand.stream()
                        .map(card -> ((BlackjackCard) card).getPoints(dealerPoints))
                        .reduce((a, b) -> a + b)
                        .orElse(0);

        player.getHand().addCards(deck.getTop(), deck.getTop());
        playerPoints +=
                player.getHand().stream()
                        .map(card -> ((BlackjackCard) card).getPoints(playerPoints))
                        .reduce((a, b) -> a + b)
                        .orElse(0);

        handsStatus = checkHands();
        if (handsStatus == HandsStatus.UNFINISHED) {
            ((BlackjackCard) dealerHand.getCard(1)).hide();
        }

        printRoundStartMessage();

        roundNumber++;

        playerInterface.printLinesSeparator();

        if (handsStatus == HandsStatus.UNFINISHED) {
            isPlayersTurn = false;
            changeTurn();
        }
    }

    private void playersTurn() {

        if (deck.isEmpty()) {
            playerInterface.tell(new Message("No cards in deck left"));
            printHands();
            playerInterface.printLinesSeparator();

            endRound();
        }

        String choice;
        while (true) {
            choice =
                    playerInterface
                            .ask(new Request("Enter \"1\" to take card and \"0\" to stop..."))
                            .getText();
            if (choice.equals("1") || choice.equals("0")) {
                break;
            }
        }

        if (choice.equals("1")) {
            Card takenCard = deck.getTop();

            playerInterface.tell(new Message("You opened card " + takenCard.toString()));
            player.getHand().addCard(takenCard);
            playerPoints += ((BlackjackCard) takenCard).getPoints(playerPoints);

            printHands();
            playerInterface.printLinesSeparator();
        } else {
            playerInterface.printLinesSeparator();
            changeTurn();
        }
    }

    private void dealersTurn() {}

    private void changeTurn() {
        if (isPlayersTurn) {
            playerInterface.tell(new Message("Dealers' turn"));
            playerInterface.tell(new Message("-------"));
        } else {
            playerInterface.tell(new Message("Your turn"));
            playerInterface.tell(new Message("-------"));
        }

        isPlayersTurn = !isPlayersTurn;
    }

    private HandsStatus checkHands() {
        if (isDealerEndedHisTurn) {
            int comparisonRes = Integer.signum(Integer.compare(playerPoints, dealerPoints));
            return switch (comparisonRes) {
                case 0 -> HandsStatus.DRAW;
                case 1 -> HandsStatus.PLAYER_WIN;
                case -1 -> HandsStatus.DEALER_WIN;
                default ->
                        throw new DriverException(
                                String.format(
                                        "Unexpected return from Integer.signum: %d",
                                        comparisonRes));
            };
        }

        if (playerPoints > 21) {
            return HandsStatus.DEALER_WIN;
        }
        if (dealerPoints > 21) {
            return HandsStatus.PLAYER_WIN;
        }
        if (playerPoints == 21) {
            return HandsStatus.PLAYER_WIN;
        }
        if (dealerPoints == 21) {
            return HandsStatus.DEALER_WIN;
        }

        return HandsStatus.UNFINISHED;
    }

    private void printRoundStartMessage() {
        playerInterface.tell(new Message(String.format("Round %d", roundNumber)));

        playerInterface.tell(new Message("The dealer dealt the cards"));

        printHands();
    }

    private void printHands() {
        playerInterface.tell(
                new Message(
                        "\tYour cards: "
                                + player.getHand().toString()
                                + String.format(" => %d", playerPoints)));
        playerInterface.tell(
                new Message(
                        "\tDealers' cards: "
                                + dealerHand.toString()
                                + (isDealersCardsUnhidden
                                        ? String.format(" => %d", dealerPoints)
                                        : "")));
    }

    private void endRound() {
        game.setStatus(Game.GameStatus.DONE);

        switch (handsStatus) {
            case DEALER_WIN:
                dealerWins++;
                break;
            case PLAYER_WIN:
                playerWins++;
                break;
            case DRAW:
                break;
            default:
                throw new DriverException("Driver unexcpectedly tried to end round");
        }
        playerInterface.tell(new Message(getRoundEndingString()));
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
                    case 0 -> "One more time?";
                    case 1 -> "You're winning now!";
                    case -1 -> "You'd better try harder!";
                    default ->
                            throw new DriverException(
                                    String.format(
                                            "Unexpected return from Integer.signum: %d",
                                            winsComparisonRes));
                };
    }

    private enum HandsStatus {
        UNFINISHED,
        DEALER_WIN,
        PLAYER_WIN,
        DRAW
    }
}
