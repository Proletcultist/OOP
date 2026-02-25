package ru.nsu.zenin.collection;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BlockingCircularBufferTest {
    @Test
    void fillUpBuffer() throws Exception {
        BlockingCircularBuffer<Integer> buff = new BlockingCircularBuffer<Integer>(3);

        Thread writer =
                new Thread(
                        () -> {
                            buff.put(1);
                            buff.put(2);
                            buff.put(3);

                            synchronized (buff) {
                                buff.notify();
                            }

                            buff.put(4);
                        });
        synchronized (buff) {
            writer.start();
            buff.wait();
        }

        List<Integer> readen = new ArrayList<Integer>(3);

        Thread reader =
                new Thread(
                        () -> {
                            for (int i = 0; i < 3; i++) {
                                readen.add(buff.take());
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
        expected.add(1);
        expected.add(2);
        expected.add(3);

        Assertions.assertEquals(readen, expected);
        writer.interrupt();
    }
}
