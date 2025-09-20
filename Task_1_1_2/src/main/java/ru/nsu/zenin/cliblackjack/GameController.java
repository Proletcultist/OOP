package ru.nsu.zenin.cliblackjack;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import ru.nsu.zenin.cardgame.Game;

class GameController {

    private static final ResourceBundle resources =
            ResourceBundle.getBundle("cliblackjack.Messages", Locale.getDefault());

    private GameController() {}

    static void runGame(Game game, Scanner scanner) {
        while (true) {
            game.startNextRound();

            System.out.println(resources.getString("askAnotherRound") + " (y/n)");

            String choice;
            while (true) {
                choice = scanner.nextLine();
                if (choice.equals("y") || choice.equals("n")) {
                    break;
                }
            }

            if (choice.equals("n")) {
                break;
            }
        }
    }
}
