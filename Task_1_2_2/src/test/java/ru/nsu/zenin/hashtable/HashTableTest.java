package ru.nsu.zenin.hashtable;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashTableTest {

    @Test
    void basicTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(22, "Aboba");

        Assertions.assertEquals(table.get(22), "Aboba");
    }

    @Test
    void collisionTest() {
        HashTable<String, String> table = new HashTable<String, String>();

        table.put("FB", "a");
        table.put("Ea", "b");

        Assertions.assertEquals(table.get("FB"), "a");
        Assertions.assertEquals(table.get("Ea"), "b");
    }

    @Test
    void fullLoad() {
        HashTable<String, String> table = new HashTable<String, String>();

        table.put("FB", "a");
        table.put("Ea", "b");
        table.put("Vd", "c");
        table.put("sR", "d");
        table.put("oD", "e");
        table.put("FF", "f");
        table.put("ff", "g");
        table.put("Ps", "h");
        table.put("eR", "k");
        table.put("Pq", "l");
        table.put("Gp", "m");
        table.put("sa", "n");
        table.put("AS", "o");
        table.put("wR", "p");

        Assertions.assertTrue(table.containsKey("FB"));
        Assertions.assertTrue(table.containsKey("Ea"));
        Assertions.assertTrue(table.containsKey("Vd"));
        Assertions.assertTrue(table.containsKey("sR"));
        Assertions.assertTrue(table.containsKey("oD"));
        Assertions.assertTrue(table.containsKey("FF"));
        Assertions.assertTrue(table.containsKey("ff"));
        Assertions.assertTrue(table.containsKey("Ps"));
        Assertions.assertTrue(table.containsKey("eR"));
        Assertions.assertTrue(table.containsKey("Pq"));
        Assertions.assertTrue(table.containsKey("Gp"));
        Assertions.assertTrue(table.containsKey("sa"));
        Assertions.assertTrue(table.containsKey("AS"));
        Assertions.assertTrue(table.containsKey("wR"));

        Assertions.assertFalse(table.containsKey("ABOBA"));

        Assertions.assertEquals(table.get("FB"), "a");
        Assertions.assertEquals(table.get("Ea"), "b");
        Assertions.assertEquals(table.get("Vd"), "c");
        Assertions.assertEquals(table.get("sR"), "d");
        Assertions.assertEquals(table.get("oD"), "e");
        Assertions.assertEquals(table.get("FF"), "f");
        Assertions.assertEquals(table.get("ff"), "g");
        Assertions.assertEquals(table.get("Ps"), "h");
        Assertions.assertEquals(table.get("eR"), "k");
        Assertions.assertEquals(table.get("Pq"), "l");
        Assertions.assertEquals(table.get("Gp"), "m");
        Assertions.assertEquals(table.get("sa"), "n");
        Assertions.assertEquals(table.get("AS"), "o");
        Assertions.assertEquals(table.get("wR"), "p");
    }

    @Test
    void robinHoodingTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");
        table.put(25, "d");

        Assertions.assertEquals(table.get(0), "a");
        Assertions.assertEquals(table.get(1), "b");
        Assertions.assertEquals(table.get(12), "c");
        Assertions.assertEquals(table.get(25), "d");
    }

    @Test
    void robinHoodingTest2() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        table.remove(0);

        table.put(25, "d");

        Assertions.assertNull(table.get(0));
        Assertions.assertEquals(table.get(1), "b");
        Assertions.assertEquals(table.get(12), "c");
        Assertions.assertEquals(table.get(25), "d");
    }

    @Test
    void modifyWithPutTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(0, "b");

        Assertions.assertEquals(table.get(0), "b");
    }

    @Test
    void removeNonExisting() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");

        Assertions.assertNull(table.remove(1));
        Assertions.assertEquals(table.get(0), "a");
    }

    @Test
    void removeTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        table.remove(1);

        Assertions.assertEquals(table.get(0), "a");
        Assertions.assertNull(table.get(1));
        Assertions.assertEquals(table.get(12), "c");
    }

    @Test
    void updateTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");

        table.update(0, "b");
        table.update(1, "c");

        Assertions.assertEquals(table.get(0), "b");
        Assertions.assertEquals(table.get(1), "c");
    }

    @Test
    void toStringTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        Assertions.assertEquals(table.toString(), "{(0 : a), (1 : b), (12 : c)}");
    }

    @Test
    void forEachTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        table.forEach(
                (K, V) -> {
                    switch (K) {
                        case 0:
                            Assertions.assertEquals(V, "a");
                            break;
                        case 1:
                            Assertions.assertEquals(V, "b");
                            break;
                        case 12:
                            Assertions.assertEquals(V, "c");
                            break;
                        default:
                            Assertions.fail("Unexpected key in forEach");
                    }
                });
    }

    @Test
    void forEachTest2() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        Assertions.assertThrows(
                ConcurrentModificationException.class,
                () -> {
                    table.forEach(
                            (K, V) -> {
                                if (V.equals("a")) {
                                    table.put(2, "a");
                                }
                            });
                });
    }

    @Test
    void equalsTest() {
        HashTable<Integer, String> table1 = new HashTable<Integer, String>();
        HashTable<Integer, String> table2 = new HashTable<Integer, String>();
        HashTable<Integer, Integer> table3 = new HashTable<Integer, Integer>();

        table1.put(1, "a");
        table1.put(2, "b");
        table1.put(3, "c");

        table2.put(3, "c");
        table2.put(2, "b");
        table2.put(1, "a");

        table3.put(1, 1);

        Assertions.assertEquals(table1, table2);
        Assertions.assertNotEquals(table1, table3);
        Assertions.assertNotEquals(table2, table3);
    }

    @Test
    void iterationTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        for (var entry : table) {
            switch (entry.getKey()) {
                case 0:
                    Assertions.assertEquals(entry.getValue(), "a");
                    break;
                case 1:
                    Assertions.assertEquals(entry.getValue(), "b");
                    break;
                case 12:
                    Assertions.assertEquals(entry.getValue(), "c");
                    break;
                default:
                    Assertions.fail("Unexpected key in forEach");
            }
        }
    }

    @Test
    void iteratorRemoveTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        Iterator<HashTable.Entry<Integer, String>> it = table.iterator();

        while (it.hasNext()) {
            if (it.next().getKey().equals(1)) {
                it.remove();
            }
        }

        Assertions.assertEquals(table.get(0), "a");
        Assertions.assertNull(table.get(1));
        Assertions.assertEquals(table.get(12), "c");
    }

    @Test
    void iteratorValueModTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        Iterator<HashTable.Entry<Integer, String>> it = table.iterator();

        while (it.hasNext()) {
            HashTable.Entry<Integer, String> ent = it.next();
            if (ent.getKey().equals(1)) {
                ent.setValue("e");
            }
        }

        Assertions.assertEquals(table.get(0), "a");
        Assertions.assertEquals(table.get(1), "e");
        Assertions.assertEquals(table.get(12), "c");
    }

    @Test
    void iteratorElemsExceededTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        Iterator<HashTable.Entry<Integer, String>> it = table.iterator();

        while (it.hasNext()) {
            it.next();
        }

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    it.next();
                });
    }

    @Test
    void iteratorIllegalRemovingTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        Iterator<HashTable.Entry<Integer, String>> it = table.iterator();

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    it.remove();
                });
    }

    @Test
    void iteratorWithRemovedTest() {
        HashTable<Integer, String> table = new HashTable<Integer, String>();

        table.put(0, "a");
        table.put(1, "b");
        table.put(12, "c");

        table.remove(1);

        for (var entry : table) {
            switch (entry.getKey()) {
                case 0:
                    Assertions.assertEquals(entry.getValue(), "a");
                    break;
                case 12:
                    Assertions.assertEquals(entry.getValue(), "c");
                    break;
                default:
                    Assertions.fail("Unexpected key in forEach");
            }
        }
    }
}
