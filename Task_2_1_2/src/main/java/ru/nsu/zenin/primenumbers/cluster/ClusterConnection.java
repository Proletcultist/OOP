package ru.nsu.zenin.primenumbers.cluster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class ClusterConnection implements AutoCloseable {
    private final int CONNECTION_TIMEOUT = 1000;
    private final ProtocolVersion VERSION = ProtocolVersion.LEET_VER;

    private final UUID nodeId;

    private final InetSocketAddress groupAddress;
    private final InetSocketAddress nodeAddress;

    private Thread udpThread;
    private Thread tcpThread;

    private MulticastSocket multicastSock;
    private ServerSocket tcpServer;

    private final Map<UUID, NodeConnection> nodeConnections;

    public abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    public abstract void onClose();

    public ClusterConnection(InetSocketAddress groupAddress, InetSocketAddress nodeAddress)
            throws IOException {
        this.groupAddress = groupAddress;
        this.nodeAddress = nodeAddress;
        this.nodeConnections = new ConcurrentHashMap<UUID, NodeConnection>();
        this.nodeId = UUID.randomUUID();

        startTcpServer();
        startUdpListener();

        announceNode();
    }

    // public CompletableFuture<Boolean> submit(int[] nums) {}

    @Override
    public void close() {}

    private void startTcpServer() throws IOException {
        tcpServer = new ServerSocket();
        tcpServer.bind(nodeAddress);

        tcpThread =
                Thread.ofVirtual()
                        .start(
                                () -> {
                                    while (!Thread.interrupted()) {
                                        try {
                                            Socket socket = tcpServer.accept();
                                            System.out.println(
                                                    nodeId
                                                            + ": Connected new "
                                                            + socket.getRemoteSocketAddress());
                                            addNewNodeConnection(socket);
                                        } catch (IOException ignore) {
                                            close();
                                        }
                                    }
                                });
    }

    private void startUdpListener() throws IOException {
        multicastSock = new MulticastSocket(groupAddress.getPort());
        NetworkInterface nf = NetworkInterface.getByInetAddress(nodeAddress.getAddress());
        multicastSock.setNetworkInterface(nf);
        multicastSock.joinGroup(groupAddress, nf);

        udpThread =
                Thread.ofVirtual()
                        .start(
                                () -> {
                                    byte[] buffer = new byte[512];
                                    while (!Thread.interrupted()) {
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

                                        if (msg instanceof Message.Presence p
                                                && !p.nodeId().equals(nodeId)
                                                && !nodeConnections.containsKey(p.nodeId())) {
                                            System.out.println(nodeId + ": Presence " + p.nodeId());

                                            try {
                                                Socket socket = new Socket();
                                                socket.connect(
                                                        new InetSocketAddress(
                                                                p.address(), p.port()),
                                                        CONNECTION_TIMEOUT);
                                                addNewNodeConnection(socket);
                                            } catch (IOException ignore) {
                                            }
                                        }
                                    }
                                });
    }

    private void addNewNodeConnection(Socket socket) {
        try {
            NodeConnection newConn =
                    new NodeConnection(VERSION, socket, nodeId) {
                        @Override
                        protected void onStateChange(NodeConnection.State state) {
                            System.out.println(this.getRemoteNodeId() + ": " + state);
                        }

                        @Override
                        protected void onIncomingTask(
                                int[] nums, CompletableFuture<Boolean> future) {
                            ClusterConnection.this.onIncomingTask(nums, future);
                        }
                    };
        } catch (IOException ignore) {
        }
    }

    private void announceNode() throws IOException {
        byte[] data =
                Codec.serialize(
                        VERSION,
                        new Message.Presence(
                                nodeId, nodeAddress.getAddress(), nodeAddress.getPort()));
        DatagramPacket packet = new DatagramPacket(data, data.length, groupAddress);
        multicastSock.send(packet);
    }
}
