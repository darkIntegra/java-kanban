package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createInstance();
    }

    protected abstract T createInstance() throws IOException;

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

    // Тест на пересечение задач
    @Test
    void testIsOverlappingWithAny() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(2);

        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(startTime1);
        task1.setDuration(Duration.between(startTime1, endTime1));

        LocalDateTime startTime2 = startTime1.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusHours(2);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(startTime2);
        task2.setDuration(Duration.between(startTime2, endTime2));

        taskManager.createTask(task1);

        // Попытка добавить вторую задачу должна вызвать исключение из-за пересечения временных интервалов
        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));

        String expectedMessage = "Задача пересекается по времени с другой задачей.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Не удалось обнаружить пересечение временных интервалов");

        // Теперь создадим задачу без пересечения и проверим, что она добавляется корректно
        LocalDateTime startTime3 = endTime1.plusHours(1);
        LocalDateTime endTime3 = startTime3.plusHours(2);

        Task task3 = new Task("Task 3", "Description 3");
        task3.setStartTime(startTime3);
        task3.setDuration(Duration.between(startTime3, endTime3));

        taskManager.createTask(task3);
        assertNotNull(taskManager.getTaskById(task3.getId()), "Задача не была создана");
    }
}