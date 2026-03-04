package ru.nsu.zenin.collection;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class BlockingQueueTest {
    @Test
    void fillUpBuffer() throws Exception {
        BlockingQueue<Integer> buff = new BlockingCircularBuffer<Integer>(3);

        Thread writer =
                new Thread(
                        () -> {
                            try {
                                buff.put(1);
                                buff.put(2);
                                buff.put(3);

                                synchronized (buff) {
                                    buff.notify();
                                }

                                buff.put(4);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
        synchronized (buff) {
            writer.start();
            buff.wait();
        }

        List<Integer> readen = new ArrayList<Integer>(3);

        Thread reader =
                new Thread(
                        () -> {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    readen.add(buff.take());
                                }

                                synchronized (readen) {
                                    readen.notify();
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
        synchronized (readen) {
            reader.start();
            readen.wait();
        }

        List<Integer> expected = new ArrayList<Integer>(3);
        expected.add(1);
        expected.add(2);
        expected.add(3);

        Assertions.assertEquals(readen, expected);
        writer.interrupt();
    }

    @Test
    void testInvalidCapacity() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BlockingQueue<Integer> q = new BlockingCircularBuffer(0);
                });
    }

    @ParameterizedTest
    @MethodSource("queuesSource")
    void testReadBlock(BlockingQueue<Integer> queue) throws Exception {
        MutableInt readen = new MutableInt(-1);

        Thread reader =
                new Thread(
                        () -> {
                            try {
                                readen.setValue(queue.take());
                                synchronized (readen) {
                                    readen.notify();
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

        synchronized (readen) {
            reader.start();

            Thread writer =
                    new Thread(
                            () -> {
                                try {
                                    queue.put(20);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });

            writer.start();

            readen.wait();
            Assertions.assertEquals(readen.getValue(), 20);
        }
    }

    @ParameterizedTest
    @MethodSource("queuesSource")
    void testPoll(BlockingQueue<Integer> queue) throws Exception {
        Assertions.assertNull(queue.poll());
        queue.put(10);
        Assertions.assertEquals(queue.poll(), 10);
    }

    @ParameterizedTest
    @MethodSource("queuesSource")
    void testBlockUntilEmpty(BlockingQueue<Integer> queue) throws Exception {
        queue.put(10);

        Thread writer =
                new Thread(
                        () -> {
                            try {
                                queue.blockUntilEmpty();
                                queue.put(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

        writer.start();

        List<Integer> readen = new ArrayList<Integer>();

        Thread reader =
                new Thread(
                        () -> {
                            try {
                                readen.add(queue.take());
                                readen.add(queue.take());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            synchronized (readen) {
                                readen.notify();
                            }
                        });

        synchronized (readen) {
            reader.start();
            readen.wait();
        }

        List<Integer> expected = new ArrayList<Integer>(3);
        expected.add(10);
        expected.add(100);

        Assertions.assertEquals(readen, expected);
    }

    static List<BlockingQueue> queuesSource() {
        List<BlockingQueue> out = new ArrayList<BlockingQueue>(2);
        out.add(new BlockingCircularBuffer<Integer>(100));
        out.add(new BlockingLinkedList<Integer>());

        return out;
    }
}
