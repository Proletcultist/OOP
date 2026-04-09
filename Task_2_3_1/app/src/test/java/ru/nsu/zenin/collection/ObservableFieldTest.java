package ru.nsu.zenin.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObservableFieldTest {
    @Test
    void constructorTest() {
        ObservableField<Integer> field = new ObservableField<Integer>(1, 2, 2);
        field.forEach((p, i) -> {
            Assertions.assertEquals(i, 1);
        });
    }

    @Test
    void gettersSettersTest() {
        ObservableField<Integer> field = new ObservableField<Integer>(1, 2, 3);
        Assertions.assertEquals(field.getWidth(), 2);
        Assertions.assertEquals(field.getHeight(), 3);

        field.set(new Point2D(0, 0), 2);

        Assertions.assertEquals(field.get(new Point2D(0, 0)), 2);

        field.setAll(5);

        field.forEach((p, i) -> {
            Assertions.assertEquals(i, 5);
        });
    }

    @Test
    void listenerTest() {
        ObservableField<Integer> field = new ObservableField<Integer>(1, 2, 3);

        field.addListener(c -> {
            Assertions.assertEquals(c.point(), new Point2D(1, 1));
            Assertions.assertEquals(c.state(), 88);
        });

        field.set(new Point2D(1, 1), 88);
    }

    @Test
    void resizeTest() {
        ObservableField<Integer> field = new ObservableField<Integer>(1, 2, 3);

        field.resize(2, 3, 4);

        field.forEach((p, i) -> {
            if (p.x() == 2 || p.y() == 3) {
                Assertions.assertEquals(i, 2);
            }
            else {
                Assertions.assertEquals(i, 1);
            }
        });
    }
}
