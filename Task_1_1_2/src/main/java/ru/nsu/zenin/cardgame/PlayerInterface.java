package ru.nsu.zenin.cardgame;

public interface PlayerInterface {

    void tell(Message message);

    Response ask(Request req);

    void printLinesSeparator();
}
