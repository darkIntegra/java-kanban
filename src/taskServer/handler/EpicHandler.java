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
            StringBuilder response = new StringBuilder();

            if (query == null || query.isEmpty()) {
                Collection<Epic> allTasks = taskManager.getEpics();
                for (Epic task : allTasks) {
                    response.append(task.toString()).append("\n");
                }
            } else {
                int taskId = getTaskIdFromRequest(query);
                Epic task = taskManager.getEpicById(taskId);

                if (query.contains("/subtasks")) {
                    List<Integer> subtaskIds = task.getSubtaskIds();
                    response.append("Subtask IDs: ").append(subtaskIds.toString()).append("\n");
                } else {
                    response.append(task.toString());
                }
            }
            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange);
        }
    }


    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            Epic newEpic = readTaskFromRequest(exchange);

            // Проверяем, передается ли ID в JSON
            if (newEpic.getId() == -1) {  // -1 означает, что ID должен быть сгенерирован
                taskManager.createEpic(newEpic); // Создаем новую задачу через TaskManager
                String response = "Задача успешно добавлена.";
                sendText(exchange, response, 201); // Отправляем код 201 Created
            } else {
                // Если ID не равен -1, возвращаем ошибку
                throw new BadRequestException("Ошибка: нельзя отправлять задачу с установленным ID!");
            }
        } catch (JsonParseException | BadRequestException e) {
            handleErrorResponse(e, 400, exchange); // Некорректный запрос
        } catch (ManagerValidatePriorityException e) {
            handleErrorResponse(e, 406, exchange); // Недопустимые данные
        }
    }

    @Override
    protected void handlePut(String query, HttpExchange exchange) throws IOException {
        try {
            int taskId = getTaskIdFromRequest(query);

            // Проверяем, существует ли эпик с указанным ID
            if (!taskManager.containsEpic(taskId)) {
                sendText(exchange, "Эпик с ID " + taskId + " не найден.", 404);
                return;
            }

            Epic updatedEpic = readTaskFromRequest(exchange);
            updatedEpic.setId(taskId);
            taskManager.updateEpic(updatedEpic);
            sendText(exchange, "Эпик с ID " + taskId + " успешно обновлён.", 200); // Исправлен код ответа на 200
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException
                 | BadRequestException e) {
            handleErrorResponse(e, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange);
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