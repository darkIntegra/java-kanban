package manager;

import exception.ManagerLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("tasksFile", ".csv").toFile();
        taskManager = createInstance();
    }

    @AfterEach
    void tearDown() {
        tempFile.deleteOnExit();
    }

    @Override
    protected FileBackedTaskManager createInstance() {
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoadFromFile() throws ManagerLoadException {
        // Создаем задачи
        Task task1 = new Task("Имя Таск1", "Описание задачи Таск1");
        Epic epic1 = new Epic("Имя Эпик1", "Описание задачи Эпик1");
        Subtask subtask1 = new Subtask("Имя Сабтаск1", "Описание задачи Сабтаск1");

        // Добавляем задачи
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1, epic1.getId());

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем загруженные данные
        assertNotNull(loadedManager.getTaskById(task1.getId()), "Задача не найдена после загрузки");
        assertNotNull(loadedManager.getEpicById(epic1.getId()), "Эпик не найден после загрузки");
        assertNotNull(loadedManager.getSubtaskById(subtask1.getId()), "Сабтаск не найден после загрузки");

        assertEquals(task1.getName(), loadedManager.getTaskById(task1.getId()).getName(),
                "Название задачи не совпадает");
        assertEquals(epic1.getName(), loadedManager.getEpicById(epic1.getId()).getName(),
                "Название эпика не совпадает");
        assertEquals(subtask1.getName(), loadedManager.getSubtaskById(subtask1.getId()).getName(),
                "Название сабтаска не совпадает");
    }

    @Test
    void testTaskAddFromFile_AddsTaskToPrioritizedTasks() throws IOException {
        // Создаем задачу и сохраняем её в файл через метод save()
        Task task = new Task("Task 1", "Description 1");
        task.setStartTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        task.setDuration(Duration.ofHours(2));

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.createTask(task);
        manager.save();

        // Загружаем задачу из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Assert: Проверяем, что задача добавлена в prioritizedTasks
        assertTrue(loadedManager.getPrioritizedTasks().contains(task));
    }

    @Test
    void testLoadFromFile_LoadsTasksFromValidFile() throws IOException {
        // Записываем задачи в файл через метод save()
        Task task = new Task("Task 1", "Description 1");
        task.setStartTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        task.setDuration(Duration.ofHours(2));

        Epic epic = new Epic("Epic 1", "Epic Description");

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.save();

        // Загружаем задачи из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи загружены корректно
        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(1, loadedManager.getEpics().size());
    }

    @Test
    void testLoadFromFile_HandlesEmptyFile() throws IOException {
        // Создаем пустой файл
        try (var writer = new java.io.FileWriter(tempFile)) {
            writer.write("");
        }

        // Проверяем, что загрузка из пустого файла не вызывает ошибок
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempFile));
    }
}