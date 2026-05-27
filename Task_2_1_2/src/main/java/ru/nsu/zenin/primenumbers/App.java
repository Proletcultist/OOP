package ru.nsu.zenin.primenumbers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.nsu.zenin.primenumbers.logging.Logger;

public class App {
    static final Pattern ADDRESS_PATTERN = Pattern.compile("(.+):(\\d+)");

    private static InetSocketAddress parseAddr(String str) throws IllegalArgumentException {
        Matcher m = ADDRESS_PATTERN.matcher(str);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid format of ip + port string");
        }

        try {
            int port = Integer.parseInt(m.group(2));
            return new InetSocketAddress(m.group(1), port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port string");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Required mode. Allowed modes: \"repl\", \"headless\"");
            System.exit(-1);
        }

        boolean repl =
                switch (args[0]) {
                    case "repl" -> true;
                    case "headless" -> false;
                    default -> {
                        System.err.println("Invaled mode. Allowed modes: \"repl\", \"headless\"");
                        System.exit(-1);
                        yield false;
                    }
                };
        InetSocketAddress groupAddress;
        InetSocketAddress nodeAddress;

        if (args.length < 2) {
            System.err.println("Required group address. Format: <host>:<port>");
            System.exit(-1);
            return;
        }
        try {
            groupAddress = parseAddr(args[1]);
            if (groupAddress.isUnresolved()) {
                throw new IllegalArgumentException("Failed to resolve hostname");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Bad group address: " + e.getMessage());
            System.exit(-1);
            return;
        }

        if (args.length < 3) {
            System.err.println("Required node address. Format: <host>:<port>");
            System.exit(-1);
            return;
        }
        try {
            nodeAddress = parseAddr(args[2]);
            if (nodeAddress.isUnresolved()) {
                throw new IllegalArgumentException("Failed to resolve hostname");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Bad node address: " + e.getMessage());
            System.exit(-1);
            return;
        }

        Logger.init(new BufferedWriter(new OutputStreamWriter(System.err)));
        Node node =
                new Node(groupAddress, nodeAddress) {
                    @Override
                    protected void onShutdown() {
                        Logger.close();
                        System.exit(0);
                    }
                };

        if (repl) {
            try (Reader reader = new BufferedReader(new InputStreamReader(System.in));
                    Writer writer = new OutputStreamWriter(System.out)) {
                node.serviceRepl(reader, writer);
            }

            // If repl ended - shutdown node
            node.shutdown();
        } else {
            // Freeze main thread until process is killed
            synchronized (node) {
                node.wait();
            }
        }
    }
}
