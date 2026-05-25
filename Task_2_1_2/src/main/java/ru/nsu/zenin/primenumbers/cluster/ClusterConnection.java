package ru.nsu.zenin.primenumbers.cluster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class ClusterConnection implements AutoCloseable {
    private final ProtocolVersion VERSION = ProtocolVersion.LEET_VER;

    private final InetSocketAddress groupAddress;
    private final InetSocketAddress nodeAddress;

    private Thread udpThread;
    private Thread tcpListenThread;

    private DatagramSocket groupSendingSock;
    private MulticastSocket groupReceivingSock;
    private ServerSocket tcpServer;

    private final Map<InetSocketAddress, NodeConnection> nodeConnections;

    public abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    public abstract void onClose();

    public ClusterConnection(InetSocketAddress groupAddress, InetSocketAddress nodeAddress)
            throws IOException {
        this.groupAddress = groupAddress;
        this.nodeAddress = nodeAddress;
        this.nodeConnections = new ConcurrentHashMap<InetSocketAddress, NodeConnection>();

        groupSendingSock = new DatagramSocket(nodeAddress);
        startTcpServer();
        startUdpListener();

        announceNode();
    }

    // public CompletableFuture<Boolean> submit(int[] nums) {}

    @Override
    public void close() {}

    private void startTcpServer() throws IOException {
        tcpServer = new ServerSocket();

        // tcpListenThread = Thread.ofVirtual();
    }

    private void startUdpListener() throws IOException {
        groupReceivingSock = new MulticastSocket(groupAddress.getPort());
        NetworkInterface nf = NetworkInterface.getByInetAddress(nodeAddress.getAddress());
        groupReceivingSock.setNetworkInterface(nf);
        groupReceivingSock.joinGroup(groupAddress, nf);

        udpThread =
                Thread.ofVirtual()
                        .start(
                                () -> {
                                    byte[] buffer = new byte[512];
                                    while (true) {
                                        DatagramPacket packet =
                                                new DatagramPacket(buffer, buffer.length);

                                        try {
                                            groupReceivingSock.receive(packet);
                                        } catch (IOException e) {
                                            close();
                                        }

                                        if (packet.getSocketAddress().equals(nodeAddress)) {
                                            continue;
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
                                                            nodeAddress
                                                                    + ": "
                                                                    + packet.getSocketAddress());
                                                } else {
                                                    System.out.println(
                                                            nodeAddress + ": Already connected");
                                                }
                                            }
                                            default -> {}
                                        }
                                    }
                                });
    }

    private void announceNode() throws IOException {
        byte[] data = Codec.serialize(VERSION, new Message.Presence());
        DatagramPacket packet = new DatagramPacket(data, data.length, groupAddress);
        groupSendingSock.send(packet);
    }
}
