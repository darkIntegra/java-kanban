package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    Epic epicBase = new Epic("эпик 1", "содержание 1");

    @Test
    void testCreateEpic() {
        manager.createEpic(epicBase);
        Epic epicFromMap = manager.getEpicById(epicBase.getId());
        Assertions.assertNotNull(epicFromMap, "Задача не найдена.");
        Assertions.assertEquals(epicBase, epicFromMap, "Задачи не совпадают.");
    }
}