package manager;

import exception.ManagerValidatePriorityException;
import exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

        // Удаляем задачу
        taskManager.deleteTask(task.getId());

        // Проверяем, что при попытке получить задачу выбрасывается NotFoundException
        Exception exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskById(task.getId());
        });

        // Проверяем сообщение исключения
        assertEquals("Задача с ID " + task.getId() + " не найдена.", exception.getMessage());
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);

        // Удаляем эпик
        taskManager.deleteEpic(epic.getId());

        // Проверяем, что при попытке получить эпик выбрасывается NotFoundException
        Exception exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getEpicById(epic.getId());
        });

        // Проверяем сообщение исключения
        assertEquals("Эпик с ID " + epic.getId() + " не найден.", exception.getMessage());
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        taskManager.createSubtask(subtask, epic.getId());

        // Удаляем подзадачу
        taskManager.deleteSubtask(subtask.getId());

        // Проверяем, что при попытке получить подзадачу выбрасывается NotFoundException
        Exception exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getSubtaskById(subtask.getId());
        });

        // Проверяем сообщение исключения
        assertEquals("Подзадача с ID " + subtask.getId() + " не найдена.", exception.getMessage());
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
        Exception exception = assertThrows(ManagerValidatePriorityException.class, () -> taskManager.createTask(task2));

        String expectedMessage = "Задача пересекается по времени с другой задачей.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Не удалось обнаружить пересечение временных " +
                "интервалов");

        // Теперь создадим задачу без пересечения и проверим, что она добавляется корректно
        LocalDateTime startTime3 = endTime1.plusHours(1);
        LocalDateTime endTime3 = startTime3.plusHours(2);

        Task task3 = new Task("Task 3", "Description 3");
        task3.setStartTime(startTime3);
        task3.setDuration(Duration.between(startTime3, endTime3));

        taskManager.createTask(task3);
        assertNotNull(taskManager.getTaskById(task3.getId()), "Задача не была создана");
    }

    @Test
    void testHistory() {
        Task task = new Task("Task", "Description");
        taskManager.createTask(task);

        // Проверяем, что задача существует перед обращением к ней
        assertNotNull(taskManager.getTaskById(task.getId()), "Задача должна существовать");

        // Первое обращение к задаче
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "История должна содержать одну задачу");

        // Второе обращение к той же задаче
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "Повторное обращение не должно " +
                "дублировать задачу");
    }

    @Test
    void testGetPrioritizedTasks() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(2);
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(startTime1);
        task1.setDuration(Duration.between(startTime1, endTime1));

        LocalDateTime startTime2 = endTime1.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusHours(2);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(startTime2);
        task2.setDuration(Duration.between(startTime2, endTime2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Должно быть две задачи в списке приоритетов");
        assertEquals(task1, prioritizedTasks.get(0), "Задача 1 должна быть первой в списке");
        assertEquals(task2, prioritizedTasks.get(1), "Задача 2 должна быть второй в списке");
    }

    @Test
    void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1");
        subtask1.setStatus(Status.NEW);
        taskManager.createSubtask(subtask1, epic.getId());

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2");
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask2, epic.getId());

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если хотя бы одна подзадача в процессе");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть DONE, если все подзадачи завершены");
    }

    @Test
    void testEpicTimeCalculation() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(2);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1");
        subtask1.setStartTime(startTime1);
        subtask1.setDuration(Duration.between(startTime1, endTime1));
        taskManager.createSubtask(subtask1, epic.getId());

        LocalDateTime startTime2 = endTime1.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusHours(2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2");
        subtask2.setStartTime(startTime2);
        subtask2.setDuration(Duration.between(startTime2, endTime2));
        taskManager.createSubtask(subtask2, epic.getId());

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(startTime1, updatedEpic.getStartTime(), "Начало эпика должно совпадать с началом " +
                "первой подзадачи");
        assertEquals(endTime2, updatedEpic.getEndTime(), "Конец эпика должен совпадать с концом " +
                "последней подзадачи");
        Duration expectedDuration = subtask1.getDuration().plus(subtask2.getDuration());
        assertEquals(expectedDuration, updatedEpic.getDuration(),
                "Продолжительность эпика должна быть суммой продолжительностей подзадач");
    }

    @Test
    void testCreateTaskWithoutTime() {
        Task task = new Task("Task", "Description");
        taskManager.createTask(task);
        assertNotNull(taskManager.getTaskById(task.getId()), "Задача без времени должна быть создана");
    }

    @Test
    void testUpdateTaskWithoutTimeChange() {
        Task task = new Task("Task", "Description");
        taskManager.createTask(task);

        Task updatedTask = new Task(task.getId(), "Updated Name", "Updated Description",
                Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Task fetchedTask = taskManager.getTaskById(task.getId());
        assertEquals(updatedTask.getName(), fetchedTask.getName(), "Название задачи должно обновиться");
        assertNull(fetchedTask.getStartTime(), "Время задачи не должно измениться, если оно не было задано");
    }
}