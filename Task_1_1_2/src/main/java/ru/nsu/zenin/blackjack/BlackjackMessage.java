package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Message;
import ru.nsu.zenin.cardgame.exception.DriverException;

/**
 * Class for transfering messages to some bots, implemented for this driver, so they can use this
 * class for sure. E.g. bot for testing (will be implemented soon.
 */
public class BlackjackMessage extends Message {

    private final MessageType type;
    private final BlackjackHand dealerHand, playerHand;
    private final int cardsInDeckLeft;
    private final boolean isDealerEndedHisTurn;

    private BlackjackMessage(Builder builder) {
        super(builder.msg);

        this.type = builder.type;
        this.dealerHand = builder.dealerHand;
        this.playerHand = builder.playerHand;
        this.cardsInDeckLeft = builder.cardsInDeckLeft;
        this.isDealerEndedHisTurn = builder.isDealerEndedHisTurn;
    }

    static Builder builder() {
        return new Builder();
    }

    enum MessageType {
        WELCOME,
        NO_CARDS_IN_DECK_LEFT,
        YOU_OPENED_CARD,
        DEALER_OPENED_CLOSED_CARD,
        DEALER_OPENED_NEW_CARD,
        TURN_CHANGE,
        NEW_ROUND_WELCOME,
        HANDS_INFO,
        DRAW,
        DEALER_WON,
        PLAYER_WON
    }

    static class Builder {
        private MessageType type;
        private BlackjackHand dealerHand, playerHand;
        private int cardsInDeckLeft;
        private boolean isDealerEndedHisTurn;
        private String msg;

        Builder type(MessageType type) {
            this.type = type;
            return this;
        }

        Builder dealerHand(BlackjackHand dealerHand) {
            this.dealerHand = dealerHand;
            return this;
        }

        Builder playerHand(BlackjackHand playerHand) {
            this.playerHand = playerHand;
            return this;
        }

        Builder cardsInDeckLeft(int cardsInDeckLeft) {
            this.cardsInDeckLeft = cardsInDeckLeft;
            return this;
        }

        Builder isDealerEndedHisTurn(boolean isDealerEndedHisTurn) {
            this.isDealerEndedHisTurn = isDealerEndedHisTurn;
            return this;
        }

        Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        BlackjackMessage build() {
            if (type == null || dealerHand == null || playerHand == null || msg == null) {
                throw new DriverException("Cannot build BlackjackMessage without all fields set");
            }
            return new BlackjackMessage(this);
        }
    }
}
