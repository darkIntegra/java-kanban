package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач в истории");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
    }

    @Test
    void testAddSameTaskTwice() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1");

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Неверное количество задач в истории");
        assertEquals(task, history.get(0), "Задача не совпадает");
    }

    @Test
    void testRemoveTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач в истории после удаления");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task3, history.get(1), "Третья задача не совпадает");
    }

    @Test
    void testRemoveNonExistentTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(999); // Удаление несуществующей задачи

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Количество задач в истории должно остаться неизменным");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач в истории");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
        assertEquals(task3, history.get(2), "Третья задача не совпадает");
    }

    @Test
    void testClearHistory() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления всех задач");
    }

    @Test
    void testAddNullTask() {
        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не должна содержать null задачу");
    }

    @Test
    void testAddFirstTask() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1");

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Неверное количество задач в истории");
        assertEquals(task, history.get(0), "Первая задача не совпадает");
    }

    @Test
    void testAddLastTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач в истории");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
    }

    @Test
    void testRemoveFirstTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(),
                "Неверное количество задач в истории после удаления первой задачи");
        assertEquals(task2, history.get(0), "Первая задача не совпадает");
        assertEquals(task3, history.get(1), "Вторая задача не совпадает");
    }

    @Test
    void testRemoveLastTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", null);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", null);
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3", null);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(),
                "Неверное количество задач в истории после удаления последней задачи");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
    }

    @Test
    void testRemoveOnlyTask() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1");

        historyManager.add(task);
        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления единственной задачи");
    }

    @Test
    void testRemoveMiddleTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(),
                "Неверное количество задач в истории после удаления средней задачи");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task3, history.get(1), "Третья задача не совпадает");
    }
}
