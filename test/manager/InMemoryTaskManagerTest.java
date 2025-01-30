package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createInstance() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    // Тесты для получения задач, эпиков и подзадач
    @Test
    void testGetTasks() {
        Task task1 = new Task("Имя Таск1", "Описание задачи Таск1");
        Task task2 = new Task("Имя Таск2", "Описание задачи Таск2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Collection<Task> tasks = taskManager.getTasks();
        assertEquals(2, tasks.size(), "Количество задач не совпадает");
        assertTrue(tasks.contains(task1), "Задача 1 отсутствует в коллекции");
        assertTrue(tasks.contains(task2), "Задача 2 отсутствует в коллекции");
    }

    @Test
    void testGetEpics() {
        Epic epic1 = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        Epic epic2 = new Epic("Имя Эпик2", "Описание задачи Эпик2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Collection<Epic> epics = taskManager.getEpics();
        assertEquals(2, epics.size(), "Количество эпиков не совпадает");
        assertTrue(epics.contains(epic1), "Эпик 1 отсутствует в коллекции");
        assertTrue(epics.contains(epic2), "Эпик 2 отсутствует в коллекции");
    }

    @Test
    void testGetSubtasks() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        Subtask subtask2 = new Subtask("Имя Сабтаск2", "Описание задачи Сабтаск2");
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        Collection<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(2, subtasks.size(), "Количество сабтасков не совпадает");
        assertTrue(subtasks.contains(subtask1), "Сабтаск 1 отсутствует в коллекции");
        assertTrue(subtasks.contains(subtask2), "Сабтаск 2 отсутствует в коллекции");
    }

    @Test
    void testGetSubtasksByEpicId() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        Subtask subtask2 = new Subtask("Имя Сабтаск2", "Описание задачи Сабтаск2");
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        ArrayList<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(2, subtasks.size(), "Количество сабтасков не совпадает");
        assertTrue(subtasks.contains(subtask1), "Сабтаск 1 отсутствует в коллекции");
        assertTrue(subtasks.contains(subtask2), "Сабтаск 2 отсутствует в коллекции");
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("Имя Таск1", "Описание задачи Таск1");
        Task task2 = new Task("Имя Таск2", "Описание задачи Таск2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Количество задач в истории не совпадает");
        assertEquals(task1, history.get(0), "Первая задача в истории не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача в истории не совпадает");
    }

    // Тесты для статуса эпика
    @Test
    void testEpicStatusAllSubtasksNew() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Содержание 2", Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpicStatus(epic.getId());
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    void testEpicStatusAllSubtasksDone() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.DONE,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Содержание 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpicStatus(epic.getId());
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    void testEpicStatusMixedSubtasks() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Содержание 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpicStatus(epic.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void testEpicStatusEmptySubtasks() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        taskManager.updateEpicStatus(epic.getId());
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    public void testEndTimeCalculation() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);
        Subtask subtask = new Subtask("Подзадача 1", "Содержание подзадачи", Status.NEW, startTime, duration);

        // Рассчитываем ожидаемое время окончания
        LocalDateTime expectedEndTime = startTime.plus(duration);

        // Выводим значения для отладки
        System.out.println("Expected End Time: " + expectedEndTime);
        System.out.println("Actual End Time: " + subtask.getEndTime());

        // Проверяем расчет времени окончания
        assertNotNull(subtask.getEndTime(), "Время окончания не должно быть null");
        assertEquals(expectedEndTime, subtask.getEndTime(), "Время окончания подзадачи рассчитано некорректно");
    }
}