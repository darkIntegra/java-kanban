package manager;

import exception.ManagerLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("tasksFile", ".csv").toFile();
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.deleteOnExit();
    }

    @Test
    void testCreateTask() {
        Task task = new Task("Имя Таск1", "Описание задачи Таск1");
        fileBackedTaskManager.createTask(task);

        assertNotNull(fileBackedTaskManager.getTaskById(task.getId()), "Задача не была создана");
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        fileBackedTaskManager.createEpic(epic);

        assertNotNull(fileBackedTaskManager.getEpicById(epic.getId()), "Эпик не был создан");
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        fileBackedTaskManager.createEpic(epic);

        Subtask subtask = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        fileBackedTaskManager.createSubtask(subtask, epic.getId());

        assertNotNull(fileBackedTaskManager.getSubtaskById(subtask.getId()), "Сабтаск не был создан");
        assertTrue(epic.getSubtaskIds().contains(subtask.getId()), "Сабтаск не добавлен в эпик");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Имя Таск1", "Описание задачи Таск1");
        fileBackedTaskManager.createTask(task);

        Task updatedTask = new Task(task.getId(), "Обновленное имя", "Обновленное описание", Status.IN_PROGRESS);
        fileBackedTaskManager.updateTask(updatedTask);

        assertEquals(updatedTask.getName(), fileBackedTaskManager.getTaskById(task.getId()).getName(), "Название задачи не обновилось");
        assertEquals(updatedTask.getDescription(), fileBackedTaskManager.getTaskById(task.getId()).getDescription(), "Описание задачи не обновилось");
        assertEquals(updatedTask.getStatus(), fileBackedTaskManager.getTaskById(task.getId()).getStatus(), "Статус задачи не обновился");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        fileBackedTaskManager.createEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(), "Обновленное имя", "Обновленное описание");
        fileBackedTaskManager.updateEpic(updatedEpic);

        assertEquals(updatedEpic.getName(), fileBackedTaskManager.getEpicById(epic.getId()).getName(), "Название эпика не обновилось");
        assertEquals(updatedEpic.getDescription(), fileBackedTaskManager.getEpicById(epic.getId()).getDescription(), "Описание эпика не обновилось");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Имя Таск1", "Описание задачи Таск1");
        fileBackedTaskManager.createTask(task);

        fileBackedTaskManager.deleteTask(task.getId());
        assertNull(fileBackedTaskManager.getTaskById(task.getId()), "Задача не была удалена");
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        fileBackedTaskManager.createEpic(epic);

        fileBackedTaskManager.deleteEpic(epic.getId());
        assertNull(fileBackedTaskManager.getEpicById(epic.getId()), "Эпик не был удален");
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        fileBackedTaskManager.createEpic(epic);

        Subtask subtask = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");
        fileBackedTaskManager.createSubtask(subtask, epic.getId());

        fileBackedTaskManager.deleteSubtask(subtask.getId());
        assertNull(fileBackedTaskManager.getSubtaskById(subtask.getId()), "Сабтаск не был удален");
    }

    @Test
    void testSaveAndLoadFromFile() throws ManagerLoadException {
        // Создаем задачи
        Task task1 = new Task("Имя Таск1", "Описание задачи Таск1");
        Epic epic1 = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        Subtask subtask1 = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");

        // Добавляем задачи
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubtask(subtask1, epic1.getId());

        // Сохраняем в файл
        fileBackedTaskManager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем загруженные данные
        assertNotNull(loadedManager.getTaskById(task1.getId()), "Задача не найдена после загрузки");
        assertNotNull(loadedManager.getEpicById(epic1.getId()), "Эпик не найден после загрузки");
        assertNotNull(loadedManager.getSubtaskById(subtask1.getId()), "Сабтаск не найден после загрузки");

        assertEquals(task1.getName(), loadedManager.getTaskById(task1.getId()).getName(), "Название задачи не совпадает");
        assertEquals(epic1.getName(), loadedManager.getEpicById(epic1.getId()).getName(), "Название эпика не совпадает");
        assertEquals(subtask1.getName(), loadedManager.getSubtaskById(subtask1.getId()).getName(), "Название сабтаска не совпадает");
    }
}