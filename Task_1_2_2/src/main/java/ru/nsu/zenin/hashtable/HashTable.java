package ru.nsu.zenin.hashtable;

import java.util.ConcurrentModificationException;
import java.util.function.BiConsumer;
import ru.nsu.zenin.util.PrimeIntGenerator;

public class HashTable<K, V> {

    private static final int INIT_TABLE_SIZE = 13;
    private static final int EXTENSION = 2;
    private static final double MAX_LOAD_FACTOR = 0.9;

    private Entry<K, V>[] arr;
    private int elemsAmount;

    private int modCounter = 0;

    public HashTable() {
        arr = (Entry<K, V>[]) new Entry[INIT_TABLE_SIZE];
        elemsAmount = 0;
    }

    public void put(K key, V value) {
        checkLoadFactor();

        Entry<K, V>[] tab = arr;

        Entry<K, V> neww = new Entry<K, V>(key, value, key.hashCode(), false);
        elemsAmount++;

        int pos = neww.getHash() % tab.length;
        int dist = 0;

        while (true) {
            // If slot is empty
            if (tab[pos] == null) {
                tab[pos] = neww;
                break;
            }

            // Element with such key already in table
            if (!tab[pos].isTombstone()
                    && tab[pos].getHash() == neww.getHash()
                    && tab[pos].getKey().equals(neww.getKey())) {
                elemsAmount--;
                tab[pos].setValue(neww.getValue());
                break;
            }

            // If current element has probe dist less than current dist,
            // steal its' slot!!!
            if (getProbeDist(tab[pos].getHash(), pos) < dist) {
                if (tab[pos].isTombstone()) {
                    tab[pos] = neww;
                    break;
                }

                // Swap and find new place for swaped elem
                Entry<K, V> tmp = tab[pos];
                tab[pos] = neww;
                neww = tmp;

                dist = getProbeDist(neww.getHash(), pos);
            }

            pos = (pos + 1) % tab.length;
            dist++;
        }

        modCounter++;
    }

    public V remove(K key) {
        Entry<K, V> elem = find(key);

        if (elem == null) {
            return null;
        }

        elem.makeTombstone();
        elemsAmount--;
        modCounter++;

        return elem.getValue();
    }

    public V get(K key) {
        Entry<K, V> elem = find(key);

        if (elem == null) {
            return null;
        }

        return elem.getValue();
    }

    public void update(K key, V value) {
        Entry<K, V> elem = find(key);

        if (elem == null) {
            put(key, value);
            return;
        }

        elem.setValue(value);
        modCounter++;
    }

    public boolean containsKey(K key) {
        Entry<K, V> elem = find(key);

        if (elem == null) {
            return false;
        }

        return true;
    }

    public void forEach(BiConsumer<K, V> consumer) {
        Entry<K, V>[] tab = arr;
        int initialMod = modCounter;

        for (int i = 0; i < tab.length; i++) {
            if (tab[i] != null && !tab[i].isTombstone()) {
                consumer.accept(tab[i].getKey(), tab[i].getValue());
            }
        }
        if (initialMod != modCounter) {
            throw new ConcurrentModificationException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof HashTable)) {
            return false;
        }

        HashTable other = (HashTable) obj;
        Entry<K, V>[] tab = arr;

        for (int i = 0; i < tab.length; i++) {
            if (tab[i] != null && !tab[i].isTombstone()) {
                if (!other.containsKey(tab[i].getKey())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append('{');

        Entry<K, V>[] tab = arr;
        boolean first = true;

        for (int i = 0; i < tab.length; i++) {
            if (tab[i] != null && !tab[i].isTombstone()) {
                if (first) {
                    out.append("(" + tab[i].getKey() + " : " + tab[i].getValue() + ")");
                    first = false;
                } else {
                    out.append(", (" + tab[i].getKey() + " : " + tab[i].getValue() + ")");
                }
            }
        }

        out.append('}');

        return out.toString();
    }

    private int getProbeDist(int hash, int index) {
        int size = arr.length;
        int desired = hash % size;
        return desired <= index ? index - desired : size - (desired - index);
    }

    private Entry<K, V> find(K key) {
        if (elemsAmount == 0) {
            return null;
        }

        Entry<K, V>[] tab = arr;

        int hash = key.hashCode();
        int pos = hash % tab.length;
        int dist = 0;

        while (true) {
            if (tab[pos] == null) {
                return null;
            } else if (!tab[pos].isTombstone()
                    && tab[pos].getHash() == hash
                    && tab[pos].getKey().equals(key)) {
                return tab[pos];
            } else if (getProbeDist(tab[pos].getHash(), pos) < dist) {
                return null;
            }

            dist++;
            pos = (pos + 1) % tab.length;
        }
    }

    private void checkLoadFactor() {
        if ((double) elemsAmount / (double) arr.length > MAX_LOAD_FACTOR) {
            Entry<K, V>[] old = arr;

            int nextSize = PrimeIntGenerator.nextPrime(old.length * EXTENSION);

            if (nextSize < old.length) {
                throw new RuntimeException("Max int value for table size exceeded");
            }

            arr = (Entry<K, V>[]) new Entry[nextSize];
            elemsAmount = 0;

            for (int i = 0; i < old.length; i++) {
                if (old[i] != null && !old[i].isTombstone()) {
                    put(old[i].getKey(), old[i].getValue());
                }
            }
        }
    }

    private class Entry<K, V> {
        private K key;
        private V value;
        private final int hash;
        private boolean tombstone;

        public Entry(K key, V value, int hash, boolean tombstone) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.tombstone = tombstone;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public int getHash() {
            return hash;
        }

        public boolean isTombstone() {
            return tombstone;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public void makeTombstone() {
            this.key = null;
            this.value = null;
            tombstone = true;
        }
    }
}
