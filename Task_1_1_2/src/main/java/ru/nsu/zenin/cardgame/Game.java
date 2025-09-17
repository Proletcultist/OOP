package ru.nsu.zenin.cardgame;

public class Game {
    private final Deck deck;
    private final PlayerInterface[] playerInterfaces;
    private final Driver driver;
    private GameStatus status = GameStatus.UNINITIALIZED;

    private Game(Builder builder) {
        deck = builder.deck != null ? builder.deck : new Deck();
        playerInterfaces =
                builder.playerInterfaces != null
                        ? builder.playerInterfaces
                        : new PlayerInterface[] {};
        driver = builder.driver;

        if (driver == null) {
            throw new NullPointerException("Cannot run game without a driver");
        }
    }

    public GameStatus getStatus() {
        return status;
    }

    public Deck getDeck() {
        return deck;
    }

    public Player[] getPlayerInterfaces() {
        return playerInterfaces;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void startNextRound() {
        if (status == GameStatus.UNINITIALIZED) {
            driver.initializeGame(this);
        } else if (status == GameStatus.DONE) {
            driver.initializeNextRound();
        } else {
            return;
        }

        status = GameStatus.RUNNING;

        while (status == GameStatus.RUNNING) {
            driver.step();
        }
    }

    public static Builder builder() {
        return new Game.Builder();
    }

    public static class Builder {
        private Deck deck;
        private Player[] playerInterfaces;
        private Driver driver;

        public Builder deck(Deck deck) {
            this.deck = deck;
            return this;
        }

        public Builder playerInterfaces(Player[] playerInterfaces) {
            this.playerInterfaces = playerInterfaces;
            return this;
        }

        public Builder driver(Driver driver) {
            this.driver = driver;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

    public enum GameStatus {
        UNINITIALIZED,
        RUNNING,
        DONE
    }
}
