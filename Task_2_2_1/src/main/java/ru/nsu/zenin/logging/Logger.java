package ru.nsu.zenin.logging;

import java.io.PrintWriter;
import java.io.Writer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.zenin.logging.exception.IllegalLoggerStateException;

public class Logger {
    private static PrintWriter os = null;
    @Getter private static boolean initialized = false;

    private Logger() {}

    public static void init(Writer osArg) {
        os = new PrintWriter(osArg, true);
        initialized = true;
    }

    public static void log(LogLevel level, String message) {
        if (os == null) {
            throw new IllegalLoggerStateException("Cannot log to uninitialized logger");
        }
        synchronized (os) {
            os.println(level + " " + message);
        }
    }

    public static void close() throws IllegalLoggerStateException {
        if (os == null) {
            throw new IllegalLoggerStateException("Cannot close uninitialized logger");
        }
        synchronized (os) {
            os.close();
            os = null;
            initialized = false;
        }
    }

    @RequiredArgsConstructor
    public enum LogLevel {
        INFO("[\033[34mInfo\033[0m]"),
        WARNING("[\033[33mWarning\033[0m]"),
        ERROR("[\033[31mError\033[0m]");

        private final String asString;

        @Override
        public String toString() {
            return asString;
        }
    }
}
