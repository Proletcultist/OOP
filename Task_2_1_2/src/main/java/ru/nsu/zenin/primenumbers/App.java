package ru.nsu.zenin.primenumbers;

import java.net.InetAddress;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.exit(1);
        }

        String mode = args[0].toLowerCase();
        String ipStr = args[1];
        int tcpPort;

        try {
            tcpPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[2]);
            return;
        }

        InetAddress ip = InetAddress.getByName(ipStr);
        Node node = new Node(ip, tcpPort);
        node.start();

        System.out.println("Node successfully started on " + ipStr + ":" + tcpPort);

        if ("master".equals(mode)) {
            runAsMaster(node);
        } else if ("worker".equals(mode)) {
            runAsWorker();
        } else {
            System.err.println("Unknown mode: '" + mode + "'. Use 'master' or 'worker'.");
            System.exit(1);
        }
    }

    private static void runAsMaster(Node node) throws Exception {
        int[] testArray = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
            89, 97, 100
        };

        System.out.println("Submitting task to the cluster...");
        System.out.println("Array: " + Arrays.toString(testArray));

        boolean hasComposite = node.submitTask(testArray);

        System.out.println("--------------------------------------------------");
        if (hasComposite) {
            System.out.println("The array contains at least one COMPOSITE number!");
        } else {
            System.out.println("The array contains ONLY PRIME numbers!");
        }

        System.exit(0);
    }

    private static void runAsWorker() throws InterruptedException {
        System.out.println("Listening for tasks...");
        System.out.println("Press Ctrl+C to shut down.");

        Thread.currentThread().join();
    }
}
