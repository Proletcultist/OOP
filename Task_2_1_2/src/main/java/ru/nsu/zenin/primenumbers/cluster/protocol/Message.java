package ru.nsu.zenin.primenumbers.cluster.protocol;

import java.util.UUID;

public sealed interface Message {
    MessageType getType();

    record Presence() implements Message {
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
}
