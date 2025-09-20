package ru.nsu.zenin.cliblackjack;

import java.util.Scanner;
import ru.nsu.zenin.blackjack.BlackjackDriver;
import ru.nsu.zenin.cardgame.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Game game =
                Game.builder()
                        .deck(StandardDecks.fullFrenchSuitedDeck())
                        .playerInterfaces(new PlayerInterface[] {new CliPlayerInterface(scanner)})
                        .driver(new BlackjackDriver())
                        .build();

	GameController.runGame(game, scanner);

        scanner.close();
    }
}
