package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Request;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackRequest extends Request {
    private final BlackjackHand dealerHand, playerHand;

    private BlackjackRequest(Builder builder) {
        super(builder.msg);

        this.dealerHand = builder.dealerHand;
        this.playerHand = builder.playerHand;
    }

    Builder builder() {
        return new Builder();
    }

    class Builder {

        private BlackjackHand dealerHand, playerHand;
        private String msg;

        Builder dealerHand(BlackjackHand dealerHand) {
            this.dealerHand = dealerHand;
            return this;
        }

        Builder playerHand(BlackjackHand playerHand) {
            this.playerHand = playerHand;
            return this;
        }

        Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        BlackjackRequest build() {
            if (dealerHand == null || playerHand == null || msg == null) {
                throw new DriverException("Cannot build BlackjackRequest without all fields set");
            }

            return new BlackjackRequest(this);
        }
    }
}
