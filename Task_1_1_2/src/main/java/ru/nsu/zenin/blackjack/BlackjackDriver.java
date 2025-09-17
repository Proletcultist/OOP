package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Card.Rank.*;
import ru.nsu.zenin.cardgame.Deck;
import ru.nsu.zenin.cardgame.Driver;
import ru.nsu.zenin.cardgame.Game;
import ru.nsu.zenin.cardgame.Message;
import ru.nsu.zenin.cardgame.PlayerInterface;
import ru.nsu.zenin.cardgame.Request;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackDriver implements Driver {

    private boolean isPlayersTurn = true;
    private boolean isDealersCardsUnhidden = false;
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

    public void initializeNextRound() {
        while (player.getHand().size() != 0) {
            deck.putOnTop(((BlackjackCard) player.getHand().removeCard(0)).cleared());
        }
        while (dealer.getHand().size() != 0) {
            deck.putOnTop(((BlackjackCard) dealer.getHand().removeCard(0)).cleared());
        }
        isPlayersTurn = true;
        isDealerEndedHisTurn = false;
        isDealersCardsUnhidden = false;

        player.setPointToZero();
        dealer.setPointToZero();

        roundNumber++;

        playerInterface.printLinesSeparator();

        startRound();
    }

    private void startRound() {
        deck.shuffle();

        dealer.getHand()
                .addCards(
                        ((BlackjackCard) deck.getTop()).withOwner(dealer),
                        ((BlackjackCard) deck.getTop()).withOwner(dealer));

        player.getHand()
                .addCards(
                        ((BlackjackCard) deck.getTop()).withOwner(player),
                        ((BlackjackCard) deck.getTop()).withOwner(player));

        handsStatus = checkHands();
        if (handsStatus == HandsStatus.UNFINISHED) {
            ((BlackjackCard) dealer.getHand().getCard(1)).hide();
        } else {
            isDealersCardsUnhidden = true;
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
            playerInterface.tell(new Message("No cards in deck left"));
            printHands();
            playerInterface.printLinesSeparator();

            changeTurn();
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
            player.getHand().addCard(((BlackjackCard) takenCard).withOwner(player));

            printHands();
            playerInterface.printLinesSeparator();
        } else {
            playerInterface.printLinesSeparator();
            changeTurn();
        }
    }

    private void dealersTurn() {

        if (!isDealersCardsUnhidden) {
            ((BlackjackCard) dealer.getHand().getCard(1)).unhide();
            isDealersCardsUnhidden = true;

            playerInterface.tell(
                    new Message(
                            "Dealer opened closed card " + dealer.getHand().getCard(1).toString()));
            printHands();
            playerInterface.printLinesSeparator();
        }

        if (deck.isEmpty()) {
            playerInterface.tell(new Message("No cards in deck left"));
            printHands();
            playerInterface.printLinesSeparator();

            isDealerEndedHisTurn = true;
            endRound();
        }

        if (dealer.getPoints() < 17) {
            Card takenCard = deck.getTop();

            playerInterface.tell(new Message("Dealer opened card " + takenCard.toString()));
            dealer.getHand().addCard(((BlackjackCard) takenCard).withOwner(dealer));

            printHands();
            playerInterface.printLinesSeparator();
        } else {
            isDealerEndedHisTurn = true;
            handsStatus = checkHands();
            endRound();
        }
    }

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
            int comparisonRes =
                    Integer.signum(Integer.compare(player.getPoints(), dealer.getPoints()));
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

        if (player.getPoints() > 21) {
            return HandsStatus.DEALER_WIN;
        }
        if (dealer.getPoints() > 21) {
            return HandsStatus.PLAYER_WIN;
        }
        if (player.getPoints() == 21) {
            return HandsStatus.PLAYER_WIN;
        }
        if (dealer.getPoints() == 21) {
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
                                + String.format(" => %d", player.getPoints())));
        playerInterface.tell(
                new Message(
                        "\tDealers' cards: "
                                + dealer.getHand().toString()
                                + (isDealersCardsUnhidden
                                        ? String.format(" => %d", dealer.getPoints())
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

    private enum HandsStatus {
        UNFINISHED,
        DEALER_WIN,
        PLAYER_WIN,
        DRAW
    }
}
