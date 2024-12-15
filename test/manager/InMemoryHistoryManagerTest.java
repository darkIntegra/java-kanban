package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    static Task task1 = new Task("таска 1", "содержание 1");
    static Epic epic1 = new Epic("эпик 1", "содержание 1");
    static Subtask subtask1 = new Subtask("сабтаск1.1", "содержание 1");

    HistoryManager history = Managers.getDefaultHistory();

    @BeforeAll
    static void created() {
        TaskManager manager = Managers.getDefault();
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, epic1.getId());
    }

    @Test
    void addNotSave() {
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        Assertions.assertNotNull(history.getHistory(), "История не сохраняется.");
        assertEquals(3, history.getHistory().size(), "Количество просмотров не равно количеству в списке просмотров.");
    }

    @Test
    void returnEmptyList() {
        assertEquals(0, history.getHistory().size(), "Изначально история просмотров не пуста.");
    }

    @Test
    public void shouldReturnTrueIfHistoryHaveCorrectOrder() {
        history.add(task1);
        history.add(epic1);
        history.add(task1);

        assertEquals(2, history.getHistory().size(), "История должна содержать 2 задачи без дубликатов.");
        assertEquals(epic1, history.getHistory().getFirst(), "Первая задача должна быть epic2.");
        assertEquals(task1, history.getHistory().getLast(), "Вторая задача должна быть task1.");
    }

}