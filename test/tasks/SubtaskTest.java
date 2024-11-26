package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    @Test
    void subtaskEqualFieldShouldBeEquals() {
        Epic epic1 = new Epic(1, "эпик 1", "содержание 1");
        Subtask subtask1 = new Subtask(2, "сабтаск 1", "содержание 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(2, "сабтаск 1", "содержание 1", Status.NEW, epic1.getId());
        Assertions.assertEquals(subtask1, subtask2, "Задачи не совпадают.");
    }
}