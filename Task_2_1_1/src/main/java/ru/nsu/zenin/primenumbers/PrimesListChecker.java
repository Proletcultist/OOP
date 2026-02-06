package ru.nsu.zenin.primenumbers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class PrimesListChecker {
    private PrimesListChecker() {}

    public static boolean isAnyCompoundInListSequentially(List<Integer> list) {
        for (int i : list) {
            if (!isPrime(i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyCompoundInListMT(List<Integer> list, int threadsAmount) {
        int quotion = list.size() / threadsAmount;
        int remainder = list.size() % threadsAmount;

        MutableBoolean result = new MutableBoolean();
        AtomicInteger aliveThreads = new AtomicInteger(threadsAmount);

        synchronized (result) {
            ThreadGroup workers = new ThreadGroup("workers");

            int lastEnd = 0;
            for (int i = 0; i < threadsAmount; i++) {
                List<Integer> threadSublist =
                        list.subList(lastEnd, lastEnd + quotion + (i < remainder ? 1 : 0));
                Thread thread =
                        new Thread(
                                workers,
                                () -> {
                                    for (int j : threadSublist) {
                                        if (!isPrime(j)) {
                                            synchronized (result) {
                                                result.setTrue();
                                                result.notify();
                                            }
                                        }
                                    }
                                    if (aliveThreads.decrementAndGet() == 0) {
                                        synchronized (result) {
                                            result.notify();
                                        }
                                    }
                                });
                thread.start();
                lastEnd += quotion + (i < remainder ? 1 : 0);
            }

            try {
                result.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            workers.interrupt();
        }

        return result.get();
    }

    public static boolean isAnyCompoundInListMT(List<Integer> list) {
        return isAnyCompoundInListMT(list, Runtime.getRuntime().availableProcessors());
    }

    public static boolean isAnyCompoundInListPS(List<Integer> list) {
        return list.parallelStream().anyMatch((i) -> !isPrime(i));
    }

    private static boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
