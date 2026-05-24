package ru.nsu.zenin.primenumbers.cluster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class ClusterConnection implements AutoCloseable {
    // We must be able to asynchronously:
    // 1. Discover nodes
    // 2. And then supply task
    // 3. And then track teh task
    private final ProtocolVersion VERSION = ProtocolVersion.LEET_VER;

    private final InetSocketAddress groupAddress;
    private final InetSocketAddress nodeAddress;

    private Thread udpThread;
    private Thread tcpListenThread;

    private MulticastSocket multicastSock;
    private ServerSocket tcpServer;
    private final Map<InetSocketAddress, NodeConnection> nodeConnections;

    public abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    public abstract void onClose();

    public ClusterConnection(InetSocketAddress groupAddress, InetSocketAddress nodeAddress)
            throws IOException {
        this.groupAddress = groupAddress;
        this.nodeAddress = nodeAddress;
        this.nodeConnections = new ConcurrentHashMap<InetSocketAddress, NodeConnection>();

        startTcpServer();
        startUdpListener();

        announceNode();
    }

    // public CompletableFuture<Boolean> submit(int[] nums) {}

    @Override
    public void close() {}

    private void startUdpListener() throws IOException {
        multicastSock = new MulticastSocket(nodeAddress);
        multicastSock.joinGroup(groupAddress.getAddress());

        udpThread =
                Thread.ofVirtual()
                        .start(
                                () -> {
                                    byte[] buffer = new byte[512];
                                    while (true) {
                                        DatagramPacket packet =
                                                new DatagramPacket(buffer, buffer.length);

                                        try {
                                            multicastSock.receive(packet);
                                        } catch (IOException e) {
                                            close();
                                        }

                                        Message msg;
                                        try {
                                            msg =
                                                    Codec.deserialize(
                                                            packet.getData(),
                                                            packet.getOffset(),
                                                            packet.getLength());
                                        } catch (Exception ignore) {
                                            continue;
                                        }

                                        switch (msg) {
                                            case Message.Presence p -> {
                                                if (!nodeConnections.containsKey(
                                                        packet.getSocketAddress())) {
                                                    System.out.println(
                                                            packet.getSocketAddress()
                                                                    + Integer.toString(
                                                                            p.tcpPort()));
                                                } else {
                                                    System.out.println("Already connected");
                                                }
                                            }
                                            default -> {}
                                        }
                                    }
                                });
    }

    private void startTcpServer() throws IOException {
        tcpServer = new ServerSocket();

        // tcpListenThread = Thread.ofVirtual();
    }

    private void announceNode() throws IOException {
        byte[] data = Codec.serialize(VERSION, new Message.Presence(nodeAddress.getPort()));
        DatagramPacket packet = new DatagramPacket(data, data.length, groupAddress);
        multicastSock.send(packet);
    }
}
