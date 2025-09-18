package ru.nsu.zenin.cliblackjack;

import java.util.Scanner;
import ru.nsu.zenin.cardgame.Message;
import ru.nsu.zenin.cardgame.PlayerInterface;
import ru.nsu.zenin.cardgame.Request;
import ru.nsu.zenin.cardgame.Response;

class CliPlayerInterface implements PlayerInterface {

    private final Scanner scanner;

    public CliPlayerInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    public void tell(Message message) {
        System.out.println(message.getText());
    }

    public Response ask(Request req) {
        System.out.println(req.getText());

        String answer = scanner.nextLine();

        return new Response(answer);
    }

    public void printLinesSeparator() {
        System.out.println();
    }
}
