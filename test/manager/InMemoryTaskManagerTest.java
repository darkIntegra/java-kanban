package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void testCreateTask() {
        Task task = new Task("Имя Таск1", "Описание задачи Таск1");
        taskManager.createTask(task);

        assertNotNull(taskManager.getTaskById(task.getId()), "Задача не была создана");
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpicById(epic.getId()), "Эпик не был создан");
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        taskManager.createSubtask(subtask, epic.getId());

        assertNotNull(taskManager.getSubtaskById(subtask.getId()), "Сабтаск не был создан");
        assertTrue(epic.getSubtaskIds().contains(subtask.getId()), "Сабтаск не добавлен в эпик");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Имя Таск1", "Описание задачи Таск1");
        taskManager.createTask(task);

        Task updatedTask = new Task(task.getId(), "Обновленное имя", "Обновленное описание",
                Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        assertEquals(updatedTask.getName(), taskManager.getTaskById(task.getId()).getName(),
                "Название задачи не обновилось");
        assertEquals(updatedTask.getDescription(), taskManager.getTaskById(task.getId()).getDescription(),
                "Описание задачи не обновилось");
        assertEquals(updatedTask.getStatus(), taskManager.getTaskById(task.getId()).getStatus(),
                "Статус задачи не обновился");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(), "Обновленное имя", "Обновленное описание");
        taskManager.updateEpic(updatedEpic);

        assertEquals(updatedEpic.getName(), taskManager.getEpicById(epic.getId()).getName(),
                "Название эпика не обновилось");
        assertEquals(updatedEpic.getDescription(), taskManager.getEpicById(epic.getId()).getDescription(),
                "Описание эпика не обновилось");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Имя Таск1", "Описание задачи Таск1");
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTaskById(task.getId()), "Задача не была удалена");
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);

        taskManager.deleteEpic(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не был удален");
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        taskManager.createSubtask(subtask, epic.getId());

        taskManager.deleteSubtask(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Сабтаск не был удален");
    }

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

        ArrayList<Task> history = (ArrayList<Task>) taskManager.getHistory();
        assertEquals(2, history.size(), "Количество задач в истории не совпадает");
        assertEquals(task1, history.get(0), "Первая задача в истории не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача в истории не совпадает");
    }
}