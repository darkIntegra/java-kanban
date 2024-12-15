package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


import java.util.ArrayList;
import java.util.Collection;

class InMemoryTaskManagerTest {

    TaskManager manager = Managers.getDefault();

    Task task1 = new Task("таска 1", "содержание 1");
    Epic epic1 = new Epic("эпик 1", "содержание 1");
    Subtask subtask1 = new Subtask("сабтаск1.1", "содержание 1");

    @BeforeEach
    void created() {
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, epic1.getId());
    }

    @Test
    void getTask() {
        Collection<Task> tasks = manager.getTasks();

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertTrue(tasks.contains(task1), "Задачи не совпадают.");
    }

    @Test
    void getEpic() {
        Collection<Epic> epics = manager.getEpics();

        Assertions.assertNotNull(epics, "Эпики не возвращаются.");
        Assertions.assertEquals(1, epics.size(), "Неверное количество эпиков.");
        Assertions.assertTrue(epics.contains(epic1), "Эпики не совпадают.");
    }

    @Test
    void getSubtask() {
        Collection<Subtask> subtasks = manager.getSubtasks();

        Assertions.assertNotNull(subtasks, "Сабтаски не возвращаются.");
        Assertions.assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        Assertions.assertTrue(subtasks.contains(subtask1), "сабтаски не совпадают.");
    }

    @Test
    void deleteAllTasks() {
        manager.deleteAllTasks();
        Collection<Task> tasks = manager.getTasks();
        Assertions.assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllSubtasks() {
        manager.deleteAllSubtasks();
        Collection<Subtask> subtasks = manager.getSubtasks();
        Assertions.assertEquals(0, subtasks.size(), "Неверное количество сабтасков.");
    }

    @Test
    void deleteAllEpics() {
        manager.deleteAllEpics();
        Collection<Epic> epics = manager.getEpics();
        Assertions.assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void getTaskById() {
        Task task20 = manager.getTaskById(task1.getId());
        Assertions.assertNotNull(task20, "Задача не найдена.");
        Assertions.assertEquals(task1, task20, "Задачи не совпадают.");
    }

    @Test
    void getEpicById() {
        Epic epic20 = manager.getEpicById(epic1.getId());
        Assertions.assertNotNull(epic20, "Эпик не найден.");
        Assertions.assertEquals(epic1, epic20, "Эпики не совпадают.");
    }

    @Test
    void getSubtaskById() {
        Subtask subtask20 = manager.getSubtaskById(subtask1.getId());
        Assertions.assertNotNull(subtask20, "Субтаск не найден.");
        Assertions.assertEquals(subtask1, subtask20, "Субтаски не совпадают.");
    }

    @Test
    void createTask() {
        Assertions.assertNotNull(task1, "Задача не найдена.");
    }

    @Test
    void createEpic() {
        Assertions.assertNotNull(epic1, "Эпик не найден.");
    }

    @Test
    void createSubtask() {
        Assertions.assertNotNull(subtask1, "Субтаск не найден.");
    }

    @Test
    void updateTask() {
        int id = task1.getId();
        Task taskUpdate = new Task(id, "таска 10", "содержание 10", Status.IN_PROGRESS);
        manager.updateTask(taskUpdate);
        Task taskAfterUpdate = manager.getTaskById(id);
        Assertions.assertEquals(taskUpdate, taskAfterUpdate, "Задачи не совпадают.");
    }

    @Test
    void updateEpic() {
        int id = epic1.getId();
        Epic epicUpdate = new Epic(id, "эпик 10", "содержание 10");
        manager.updateEpic(epicUpdate);
        Epic epicAfterUpdate = manager.getEpicById(id);
        Assertions.assertEquals(epicUpdate, epicAfterUpdate, "Задачи не совпадают.");
    }

    @Test
    void updateSubtask() {
        int id = subtask1.getId();
        Subtask subtaskUpdate = new Subtask(id, "эпик 10", "содержание 10", Status.IN_PROGRESS, epic1.getId());
        manager.updateSubtask(subtaskUpdate);
        Subtask subtaskAfterUpdate = manager.getSubtaskById(id);
        Assertions.assertEquals(subtaskUpdate, subtaskAfterUpdate, "Задачи не совпадают.");
    }

    @Test
    void deleteTask() {
        int id = task1.getId();
        manager.deleteTask(id);
        Collection<Task> tasksList = manager.getTasks();
        Assertions.assertEquals(0, tasksList.size(), "Задача не удалена.");
    }

    @Test
    void deleteEpic() {
        int id = epic1.getId();
        manager.deleteEpic(id);
        Collection<Epic> epicsList = manager.getEpics();
        Assertions.assertEquals(0, epicsList.size(), "Эпик не удален.");
    }

    @Test
    void deleteSubtask() {
        int id = subtask1.getId();
        manager.deleteSubtask(id);
        Collection<Subtask> subtasksList = manager.getSubtasks();
        Assertions.assertEquals(0, subtasksList.size(), "Субтаск не удален."); //по 6 спринту ок
        Assertions.assertNotNull(epic1.getSubtaskIds(), "В Эпике список id не удален");
    }

    @Test
    void getSubtasksByEpicId() {
        ArrayList<Subtask> subtasksList = manager.getSubtasksByEpicId(epic1.getId());
        Assertions.assertEquals(1, subtasksList.size(), "Сабтаски не найдены.");
    }

    @Test
    void getHistory() {
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        manager.getHistory();
        Assertions.assertNotNull(manager.getHistory(), "История не сохраняется.");
        Assertions.assertEquals(3, manager.getHistory().size(), "История не сохраняется.");
    }
}