package ru.nsu.zenin.primenumbers.cluster.protocol;

import java.net.InetAddress;
import java.util.UUID;

public sealed interface Message {
    MessageType getType();

    record Presence(UUID nodeId, InetAddress address, int port) implements Message {
        @Override
        public MessageType getType() {
            return MessageType.PRESENCE;
        }
    }

    record TaskSubmit(UUID taskId, int[] numbers) implements Message {
        @Override
        public MessageType getType() {
            return MessageType.TASK_SUBMIT;
        }
    }

    record TaskResult(UUID taskId, boolean hasComposite) implements Message {
        @Override
        public MessageType getType() {
            return MessageType.TASK_RESULT;
        }
    }

    record TaskStop(UUID taskId) implements Message {
        @Override
        public MessageType getType() {
            return MessageType.TASK_STOP;
        }
    }

    record TaskFailed(UUID taskId) implements Message {
        @Override
        public MessageType getType() {
            return MessageType.TASK_FAILED;
        }
    }

    record Ping() implements Message {
        @Override
        public MessageType getType() {
            return MessageType.PING;
        }
    }

    record Pong() implements Message {
        @Override
        public MessageType getType() {
            return MessageType.PONG;
        }
    }

    record Handshake(UUID nodeId) implements Message {
        @Override
        public MessageType getType() {
            return MessageType.HANDSHAKE;
        }
    }
}
