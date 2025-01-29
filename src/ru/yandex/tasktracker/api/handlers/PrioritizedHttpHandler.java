package ru.yandex.tasktracker.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public PrioritizedHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGetPrioritizedTasks(exchange);
        } else {
            sendText(exchange, "Ошибка: HTTP-метод не поддерживается.", 405);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        if (prioritizedTasks.isEmpty()) {
            sendText(exchange, "Список задач пуст.", 404);
        } else {
            sendText(exchange, gson.toJson(prioritizedTasks), 200);
        }
    }
}
