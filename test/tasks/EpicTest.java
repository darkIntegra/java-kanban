package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void epicsEqualFieldShouldBeEquals() {
        Epic epic1 = new Epic(1, "эпик 1", "содержание 1");
        Epic epic2 = new Epic(1, "эпик 1", "содержание 1");
        Assertions.assertEquals(epic1, epic2, "Задачи не совпадают.");
    }
}