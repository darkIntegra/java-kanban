package taskServer.handler;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.BadRequestException;
import exception.InvalidTaskIdException;
import exception.ManagerValidatePriorityException;
import exception.NotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.stream.Collectors;

public class SubtaskHandler extends TaskHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(String query, HttpExchange exchange) throws IOException {
        try {
            if (query == null || query.isEmpty()) {
                // Получение всех подзадач
                Collection<Subtask> allTasks = taskManager.getSubtasks();
                String response = gson.toJson(allTasks); // Преобразуем в JSON
                sendText(exchange, response, 200); // Отправляем ответ с кодом 200
            } else {
                // Получение подзадачи по ID
                int taskId = getTaskIdFromRequest(query);
                Subtask task = taskManager.getSubtaskById(taskId); // Метод может выбросить NotFoundException
                String response = gson.toJson(task); // Преобразуем в JSON
                sendText(exchange, response, 200); // Отправляем ответ с кодом 200
            }
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, 400, exchange); // Обработка ошибок формата запроса
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange); // Обработка ошибки "не найдено"
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = readTaskFromRequest(exchange);
            String response;
            if (subtask.getId() <= 0) { // Создание новой подзадачи
                Epic parentEpic = taskManager.getEpicById(subtask.getEpicId());
                if (parentEpic == null) {
                    sendText(exchange, "Подзадача не может быть создана без эпика.", 404);
                    return;
                }
                taskManager.createSubtask(subtask, subtask.getEpicId());
                // Возвращаем созданный объект Subtask в формате JSON
                response = gson.toJson(subtask);
            } else { // Обновление существующей подзадачи
                if (!taskManager.containsSubtask(subtask.getId())) {
                    sendText(exchange, "Подзадача с ID " + subtask.getId() + " не существует.", 404);
                    return;
                }
                taskManager.updateSubtask(subtask);
                response = "Подзадача с ID " + subtask.getId() + " обновлена.";
            }
            sendText(exchange, response, 201); // Возвращаем 201 для обоих случаев
        } catch (JsonParseException | BadRequestException e) {
            handleErrorResponse(e, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange);
        } catch (ManagerValidatePriorityException e) {
            handleErrorResponse(e, 406, exchange);
        }
    }

    @Override
    protected void handlePut(String query, HttpExchange exchange) throws IOException {
        try {
            int taskId = getTaskIdFromRequest(query);
            if (!taskManager.containsSubtask(taskId)) { // Проверяем существование подзадачи
                throw new NotFoundException("Подзадача с ID " + taskId + " не найдена.");
            }

            Subtask updatedTask = readTaskFromRequest(exchange);
            updatedTask.setId(taskId);
            taskManager.updateSubtask(updatedTask);

            String response = "Задача с ID " + taskId + " успешно обновлена.";
            sendText(exchange, response, 201);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, 400, exchange); // Некорректный запрос
        } catch (NotFoundException e) {
            handleErrorResponse(e, 404, exchange); // Подзадача не найдена
        } catch (ManagerValidatePriorityException e) {
            handleErrorResponse(e, 406, exchange); // Конфликт приоритетов
        }
    }

    @Override
    protected void handleDelete(String query, HttpExchange exchange) throws IOException {
        try {
            int taskIdToDelete = getTaskIdFromRequest(query);
            taskManager.deleteSubtask(taskIdToDelete);
            String response = "Задача с ID: " + taskIdToDelete + " удалена.";
            sendText(exchange, response, 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, 400, exchange);
        }
    }

    @Override
    protected Subtask readTaskFromRequest(HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        if (requestBody.isEmpty()) {
            throw new BadRequestException("Ошибка: тело запроса не может быть пустым.");
        }
        return gson.fromJson(requestBody, Subtask.class);
    }
}