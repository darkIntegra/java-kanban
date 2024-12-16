package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static Task task1;
    private static Epic epic1;
    private static Subtask subtask1;

    private HistoryManager history;

    //переделал логику тестов, было неудобно по прошлой работать с замечаниями
    @BeforeEach
    void setUp() {
        history = Managers.getDefaultHistory(); // Создаю новый экземпляр HistoryManager перед каждым тестом
        history.getHistory().clear(); // Очистка истории перед каждым тестом

        task1 = new Task("таска 1", "содержание 1");
        task1.setId(1);
        epic1 = new Epic("эпик 1", "содержание 1");
        epic1.setId(2);
        subtask1 = new Subtask("сабтаск1.1", "содержание 1");
        subtask1.setId(3);
    }

    @Test
    void addNotSave() {
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);

        assertNotNull(history.getHistory(), "История не сохраняется.");
        assertEquals(3, history.getHistory().size(), "Количество просмотров не равно количеству в списке просмотров.");
    }

    @Test
    void returnEmptyList() {
        assertEquals(0, history.getHistory().size(), "Изначально история просмотров не пуста.");
    }

    @Test
    void shouldReturnTrueIfHistoryHaveCorrectOrder() {
        history.add(task1);
        history.add(epic1);
        history.add(task1); // Дубликат

        ArrayList<Task> historyList = history.getHistory();
        assertEquals(2, historyList.size(), "История должна содержать 2 задачи без дубликатов.");
        assertEquals(epic1, historyList.get(0), "Первая задача должна быть epic1.");
        assertEquals(task1, historyList.get(1), "Вторая задача должна быть task1.");
    }

    @Test
    void testAddFirstTask() {
        history.add(task1);

        ArrayList<Task> historyList = history.getHistory();
        assertEquals(1, historyList.size(), "В истории должна быть 1 задача.");
        assertEquals(task1, historyList.get(0), "Первым элементом должна быть task1.");
    }

    @Test
    void testAddLastTask() {
        history.add(epic1);
        history.add(subtask1);
        history.add(task1);

        ArrayList<Task> historyList = history.getHistory();
        assertEquals(task1, historyList.get(historyList.size() - 1), "Последней задачей должна быть task1.");
    }

    @Test
    void testRemoveFirstTask() {
        history.add(task1);
        history.add(epic1);

        history.remove(1); // Удаляю первую задачу

        ArrayList<Task> historyList = history.getHistory();
        assertEquals(1, historyList.size(), "В истории должна остаться 1 задача.");
        assertEquals(epic1, historyList.get(0), "Первой задачей должна быть epic1.");
    }

    @Test
    void testRemoveLastTask() { //потратил больше 6 часов, прежде чем понял что в add последняя нода была без привязки
        history.add(task1);
        history.add(epic1);
        history.remove(epic1.getId());
        // Проверяем, что осталась только одна задача
        ArrayList<Task> historyList = history.getHistory();
        assertEquals(1, historyList.size(), "В истории должна остаться 1 задача.");
        assertEquals(task1, historyList.get(0), "Первая задача должна быть task1.");
    }

    @Test
    void testRemoveOnlyTask() {
        history.add(task1);
        history.remove(1); // Удаляем единственную задачу

        ArrayList<Task> historyList = history.getHistory();
        assertTrue(historyList.isEmpty());
    }

    @Test
    void testRemoveMiddleTask() {
        history.add(task1);
        history.add(subtask1);
        history.add(epic1);

        history.remove(2); // Удаляем задачу из середины

        ArrayList<Task> historyList = history.getHistory();
        assertEquals(2, historyList.size());
        assertEquals(task1, historyList.get(0));
        assertEquals(epic1, historyList.get(1));
    }
}
