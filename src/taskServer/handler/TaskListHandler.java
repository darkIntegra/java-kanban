package taskServer.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;
import taskServer.HttpTaskServer;

import java.io.IOException;

public abstract class TaskListHandler extends BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public TaskListHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        try {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                handleGet(response, exchange);
            } else {
                sendUnsupportedMethod(response, exchange);
            }
        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        } finally {
            exchange.close();
        }
    }

    // Логика обработки GET-запросов остаётся абстрактной
    protected abstract void handleGet(String response, HttpExchange exchange) throws IOException;

    // Логика обработки ошибок
    protected void handleErrorResponse(Exception e, String response, int statusCode, HttpExchange exchange)
            throws IOException {
        response = e.getMessage();
        sendText(exchange, response, statusCode);
    }

    // Логика обработки неподдерживаемых методов
    protected void sendUnsupportedMethod(String response, HttpExchange exchange) throws IOException {
        response = "Метод не поддерживается.";
        sendText(exchange, response, 405);
    }
}