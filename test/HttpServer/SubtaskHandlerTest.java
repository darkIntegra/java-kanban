package HttpServer;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskServer.HttpTaskServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client;

    public SubtaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testPostSubtask() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask2", "Testing subtask2", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(1);
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);
        subtaskId2.setId(2); // Устанавливаем ID для сравнения
        checkSubtaskEquality(subtaskId2, taskList.getFirst());
    }

    @Test
    public void testPostErrValidateSubtask() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        subtaskId2.setId(2);

        LocalDateTime startTimeId3 = LocalDateTime.of(2024, 10, 1, 10, 1);
        Subtask subtaskId3 = new Subtask("Subtask2", "Testing subtask2", startTimeId3,
                Duration.ofMinutes(3), epic1.getId());
        taskJson = gson.toJson(subtaskId3);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(1);
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);
        checkSubtaskEquality(subtaskId2, taskList.getFirst());
    }

    @Test
    public void testPostSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        subtaskId2.setId(2);
        subtaskId2.setStatus(Status.DONE);
        taskJson = gson.toJson(subtaskId2);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(1);
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);
        checkSubtaskEquality(subtaskId2, taskList.getFirst());
    }

    @Test
    public void testPostSubtaskErrById() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());

        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Попытка создать подзадачу с другим ID
        subtaskId2.setId(3); // Устанавливаем новый ID
        subtaskId2.setStatus(Status.DONE);
        taskJson = gson.toJson(subtaskId2);

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(404, response.statusCode()); // Ожидаем ошибку 404
    }

    @Test
    public void testPutSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());

        // Создаем подзадачу через POST-запрос
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Получаем актуальный ID подзадачи из менеджера
        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(1);
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);
        int actualId = taskList.getFirst().getId();

        // Обновляем статус подзадачи
        subtaskId2.setId(actualId); // Используем фактический ID
        subtaskId2.setStatus(Status.DONE);
        taskJson = gson.toJson(subtaskId2);

        // Отправляем PUT-запрос
        url = URI.create("http://localhost:8080/subtasks?id=" + actualId);
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(201, response.statusCode());

        // Проверяем, что подзадача обновлена корректно
        tasksFromManager = checkSubtasksFromManager(1);
        taskList = new ArrayList<>(tasksFromManager);
        checkSubtaskEquality(subtaskId2, taskList.getFirst());
    }

    @Test
    public void testPutErrSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());

        // Создаем подзадачу через POST-запрос
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Устанавливаем ID созданной подзадачи
        subtaskId2.setId(1);

        // Попытка обновить несуществующую подзадачу
        subtaskId2.setStatus(Status.DONE);
        taskJson = gson.toJson(subtaskId2);
        url = URI.create("http://localhost:8080/subtasks?id=3"); // Несуществующий ID
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(404, response.statusCode()); // Ожидаем ошибку 404
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = createEpic();
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        LocalDateTime startTimeId3 = LocalDateTime.of(2024, 10, 1, 10, 20);
        Subtask subtaskId3 = new Subtask("Subtask2", "Testing subtask2", startTimeId3,
                Duration.ofMinutes(3), epic1.getId());
        taskJson = gson.toJson(subtaskId3);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Получаем список подзадач из менеджера
        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(2);
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);

        // Устанавливаем фактические ID для сравнения
        subtaskId2.setId(taskList.get(0).getId());
        subtaskId3.setId(taskList.get(1).getId());

        // Отправляем GET-запрос
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем идентичность подзадач
        checkSubtaskEquality(subtaskId2, taskList.get(0));
        checkSubtaskEquality(subtaskId3, taskList.get(1));
    }

    @Test
    public void testGetSubtasksById() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic1 = createEpic();

        // Создаем первую подзадачу
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> createResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode()); // Ожидаем статус 201 (Created)
        int actualId1 = gson.fromJson(createResponse.body(), Subtask.class).getId();
        subtaskId2.setId(actualId1);

        // Создаем вторую подзадачу
        LocalDateTime startTimeId3 = LocalDateTime.of(2024, 10, 1, 10, 30);
        Subtask subtaskId3 = new Subtask("Subtask2", "Testing subtask2", startTimeId3,
                Duration.ofMinutes(10), epic1.getId());
        taskJson = gson.toJson(subtaskId3);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        createResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode()); // Ожидаем статус 201 (Created)
        int actualId2 = gson.fromJson(createResponse.body(), Subtask.class).getId();
        subtaskId3.setId(actualId2);

        // Отправляем GET-запрос для первой подзадачи
        url = URI.create("http://localhost:8080/subtasks?id=" + actualId1);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode()); // Ожидаем статус 200 (OK)

        // Проверяем, что подзадачи совпадают с ожидаемыми
        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(2); // Ожидаем 2 подзадачи
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);
        checkSubtaskEquality(subtaskId2, taskList.get(0));
        checkSubtaskEquality(subtaskId3, taskList.get(1));
    }

    @Test
    public void testGetErrSubtasksById() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic1 = createEpic();

        // Создаем подзадачу
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Отправляем GET-запрос для созданной подзадачи (ID = 1)
        url = URI.create("http://localhost:8080/subtasks?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(404, response.statusCode()); // Ожидаем статус 404 (Not Found)
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic1 = createEpic();

        // Создаем первую подзадачу
        LocalDateTime startTimeId2 = LocalDateTime.of(2024, 10, 1, 10, 0);
        Subtask subtaskId2 = new Subtask("Subtask1", "Testing subtask1", startTimeId2,
                Duration.ofMinutes(5), epic1.getId());
        String taskJson = gson.toJson(subtaskId2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> createResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode()); // Ожидаем статус 201 (Created)
        int actualId1 = gson.fromJson(createResponse.body(), Subtask.class).getId();
        subtaskId2.setId(actualId1);

        // Создаем вторую подзадачу
        LocalDateTime startTimeId3 = LocalDateTime.of(2024, 10, 1, 10, 20);
        Subtask subtaskId3 = new Subtask("Subtask2", "Testing subtask2", startTimeId3,
                Duration.ofMinutes(3), epic1.getId());
        taskJson = gson.toJson(subtaskId3);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        createResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode()); // Ожидаем статус 201 (Created)
        int actualId2 = gson.fromJson(createResponse.body(), Subtask.class).getId();
        subtaskId3.setId(actualId2);

        // Удаляем первую подзадачу
        url = URI.create("http://localhost:8080/subtasks?id=" + actualId1);
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode()); // Ожидаем статус 200 (OK)

        // Проверяем, что осталась только вторая подзадача
        Collection<Subtask> tasksFromManager = checkSubtasksFromManager(1);
        List<Subtask> taskList = new ArrayList<>(tasksFromManager);
        assertEquals(1, taskList.size()); // Ожидаем одну подзадачу
        checkSubtaskEquality(subtaskId3, taskList.getFirst());
    }

    private void checkSubtaskEquality(Subtask expected, Subtask actual) {
        assertEquals(expected.getId(), actual.getId(), "ID подзадач не совпадают");
        assertEquals(expected.getType(), actual.getType(), "Типы подзадач не совпадают");
        assertEquals(expected.getName(), actual.getName(), "Некорректное имя подзадачи");
        assertEquals(expected.getDescription(), actual.getDescription(), "Некорректное описание подзадачи");
        assertEquals(expected.getStatus(), actual.getStatus(), "Статусы подзадач не совпадают");
        assertEquals(expected.getEpicId(), actual.getEpicId(), "ID эпиков у подзадач не совпадают");
        Duration taskDuration = actual.getDuration();
        assertNotNull(taskDuration, "Продолжительность подзадачи не должна быть null");
        assertEquals(expected.getDuration(), taskDuration, "Duration подзадач не совпадают");
        LocalDateTime taskStartTime = actual.getStartTime();
        assertNotNull(taskStartTime, "Время начала подзадачи не должно быть null");
        assertEquals(expected.getStartTime(), taskStartTime, "StartTime подзадач не совпадают");
    }

    private Epic createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic");
        String taskJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        epic.setId(1);
        return epic;
    }

    private Collection<Subtask> checkSubtasksFromManager(int count) {
        Collection<Subtask> tasksFromManager = manager.getSubtasks();
        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(count, tasksFromManager.size(), "Некорректное количество подзадач");
        return tasksFromManager;
    }
}