package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void taskEqualFieldShouldBeEquals() {
        Task task1 = new Task(1, "таск 1", "содержание 1");
        Task task2 = new Task(1, "таск 1", "содержание 1");
        Assertions.assertEquals(task1, task2, "Задачи не совпадают.");
    }
}