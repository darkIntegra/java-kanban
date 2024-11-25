package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

class InMemoryHistoryManagerTest {

    static TaskManager manager = Managers.getDefault();

    static Task task1 = new Task("таска 1", "содержание 1");
    static Epic epic1 = new Epic("эпик 1", "содержание 1");
    static Subtask subtask1 = new Subtask("сабтаск1.1", "содержание 1");

    HistoryManager history = Managers.getDefaultHistory();

    @BeforeAll
    static void created() {
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, epic1.getId());
    }

    @Test
    void addNotSave() {
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        history.getHistory();
        Assertions.assertNotNull(history.getHistory(), "История не сохраняется.");
        Assertions.assertEquals(3, history.getHistory().size(), "Количество просмотров не равно количеству в списке просмотров.");
    }

    @Test
    void noMore10() {
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        history.getHistory();
        Assertions.assertEquals(10, history.getHistory().size(), "Максимальный размер истории отличен от 10.");
    }

    @Test
    void returnEmptyList() {
        history.getHistory();
        Assertions.assertEquals(0, history.getHistory().size(), "Изначально история просмотров не пуста.");
    }

}