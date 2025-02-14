package taskServer.handler;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends TaskListHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(String response, HttpExchange exchange) throws IOException {
        try {
            List<Task> history = taskManager.getHistory();
            response = gson.toJson(history);
            sendText(exchange, response, 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException e) {
            handleErrorResponse(e, "Ошибка в запросе", 400, exchange);
        }
    }
}