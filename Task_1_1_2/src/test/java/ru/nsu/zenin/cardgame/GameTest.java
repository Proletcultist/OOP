package ru.nsu.zenin.cardgame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameTest {

    private int test = 0;

    @Test
    public void builderTest() {
        Game game = Game.builder().driver(new TestDriver()).build();

        Assertions.assertEquals(game.getStatus(), Game.GameStatus.UNINITIALIZED);
        Assertions.assertEquals(game.getDeck().size(), 0);
        Assertions.assertEquals(game.getPlayerInterfaces().length, 0);
    }

    @Test
    public void gameRunningTest() {
        Game game = Game.builder().driver(new TestDriver()).build();

        game.startNextRound();

        Assertions.assertEquals(test, 2);

        game.startNextRound();

        Assertions.assertEquals(test, 4);
    }

    private class TestDriver implements Driver {
        private Game game;

        public void initializeGame(Game game) {
            test = 1;
            this.game = game;
        }

        public void step() {
            test += 1;
            game.setStatus(Game.GameStatus.DONE);
        }

        public void initializeNextRound() {
            test = 3;
        }
    }
}
