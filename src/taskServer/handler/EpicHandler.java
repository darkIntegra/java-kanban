package taskServer.handler;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.BadRequestException;
import exception.InvalidTaskIdException;
import exception.ManagerValidatePriorityException;
import exception.NotFoundException;
import manager.TaskManager;
import tasks.Epic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EpicHandler extends TaskHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }



    @Override
    protected void handleGet(String query, HttpExchange exchange) throws IOException {
        try {
            String jsonResponse;

            if (query == null || query.isEmpty()) {
                Collection<Epic> allTasks = taskManager.getEpics();
                jsonResponse = gson.toJson(allTasks);
            } else {
                int taskId = getTaskIdFromRequest(query);
                Epic task = taskManager.getEpicById(taskId);

                if (query.contains("/subtasks")) {
                    List<Integer> subtaskIds = task.getSubtaskIds();
                    jsonResponse = gson.toJson(subtaskIds);
                } else {
                    jsonResponse = gson.toJson(task);
                }
            }
            sendText(exchange, jsonResponse, 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange);
        }
    }


    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            final Epic epic = readTaskFromRequest(exchange);
            final Integer id = epic.getId();

            if (id != null && id > 0) {
                // Обновление существующего эпика
                updateExistingEpic(epic, exchange);
            } else {
                // Создание нового эпика
                createNewEpic(epic, exchange);
            }
        } catch (JsonParseException | BadRequestException e) {
            handleErrorResponse(e, 400, exchange); // Некорректный запрос
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange); // Задача не найдена
        } catch (ManagerValidatePriorityException e) {
            handleErrorResponse(e, 406, exchange); // Недопустимые данные
        }
    }

    private void createNewEpic(Epic epic, HttpExchange exchange) throws IOException {
        // Устанавливаем ID как null или -1 для создания новой задачи
        epic.setId(0);
        taskManager.createEpic(epic);
        sendText(exchange, "Эпик успешно создан.", 201);
    }

    private void updateExistingEpic(Epic epic, HttpExchange exchange) throws IOException {
        try {
            taskManager.updateEpic(epic);
            sendText(exchange, "Эпик с ID " + epic.getId() + " успешно обновлён.", 200);
        } catch (NotFoundException e) {
            throw new NotFoundException("Эпик с ID " + epic.getId() + " не найден.");
        }
    }


    @Override
    protected void handleDelete(String query, HttpExchange exchange) throws IOException {
        try {
            int taskIdToDelete = getTaskIdFromRequest(query);
            taskManager.deleteEpic(taskIdToDelete);
            String response = "Задача с ID: " + taskIdToDelete + " удалена.";
            sendText(exchange, response, 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, 400, exchange);
        }
    }

    @Override
    protected Epic readTaskFromRequest(HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));

        if (requestBody.isEmpty()) {
            throw new BadRequestException("Ошибка: тело запроса не может быть пустым.");
        }

        Epic epic = gson.fromJson(requestBody, Epic.class);

        // Если ID не передан (нулевой), явно его сбрасываем
        if (epic.getId() == 0) {
            epic.setId(-1); // Устанавливаем временное значение, чтобы избежать 0
        }

        return epic;
    }
}