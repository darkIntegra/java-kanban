package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    static Epic epic1 = new Epic("эпик 1", "содержание 1");
    static Subtask subtaskBase = new Subtask("сабтаск1.1", "содержание 1");

    @Test
    void testCreateSubtask() {
        manager.createEpic(epic1);
        manager.createSubtask(subtaskBase, epic1.getId());
        Subtask subtaskFromMap = manager.getSubtaskById(subtaskBase.getId());
        Assertions.assertNotNull(subtaskFromMap, "Задача не найдена.");
        Assertions.assertEquals(subtaskBase, subtaskFromMap, "Задачи не совпадают.");
    }
}