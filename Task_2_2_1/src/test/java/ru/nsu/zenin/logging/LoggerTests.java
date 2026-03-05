package ru.nsu.zenin.logging;

import java.io.StringWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.logging.exception.IllegalLoggerStateException;

class LoggerTests {

    @Test
    void loggerInfoTest() {
        StringWriter writer = new StringWriter();
        Logger.init(writer);

        Logger.log(Logger.LogLevel.INFO, "aaaa");

        Assertions.assertEquals(writer.toString(), "[\033[34mInfo\033[0m] aaaa\n");

        Logger.close();
    }

    @Test
    void loggerWarnTest() {
        StringWriter writer = new StringWriter();
        Logger.init(writer);

        Logger.log(Logger.LogLevel.WARNING, "aaaa");

        Assertions.assertEquals(writer.toString(), "[\033[33mWarning\033[0m] aaaa\n");

        Logger.close();
    }

    @Test
    void loggerErrorTest() {
        StringWriter writer = new StringWriter();
        Logger.init(writer);

        Logger.log(Logger.LogLevel.ERROR, "aaaa");

        Assertions.assertEquals(writer.toString(), "[\033[31mError\033[0m] aaaa\n");

        Logger.close();
    }

    @Test
    void illegalStateTest() {
        Assertions.assertThrows(
                IllegalLoggerStateException.class,
                () -> {
                    Logger.log(Logger.LogLevel.INFO, "b");
                });
    }

    @Test
    void illegalStateTest2() {
        Assertions.assertThrows(
                IllegalLoggerStateException.class,
                () -> {
                    Logger.close();
                });
    }
}
