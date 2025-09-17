package ru.nsu.zenin.cardgame;

public interface PlayerInterface {

    public void tell(Message message);

    public Response ask(Request req);

    public void printLinesSeparator();
}
