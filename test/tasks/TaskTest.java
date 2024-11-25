package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    static Task taskBase = new Task("таска 1", "содержание 1");

    @Test
    void testCreateSubtask() {
        manager.createTask(taskBase);
        Task taskFromMap = manager.getTaskById(taskBase.getId());
        Assertions.assertNotNull(taskFromMap, "Задача не найдена.");
        Assertions.assertEquals(taskBase, taskFromMap, "Задачи не совпадают.");
    }
}