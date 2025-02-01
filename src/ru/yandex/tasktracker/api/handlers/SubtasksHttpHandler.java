package ru.yandex.tasktracker.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtasksHttpHandler extends TasksHttpHandler {

    public SubtasksHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handleGetTask(HttpExchange exchange, String path) throws IOException {
        if ("/subtasks".equals(path)) {
            sendText(exchange, gson.toJson(taskManager.getSubtasks()), 200);
        } else {
            int id = extractIdFromPath(path);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) {
                sendNotFound(exchange);
            } else {
                sendText(exchange, gson.toJson(subtask), 200);
            }
        }
    }

    @Override
    public void handlePostTask(HttpExchange exchange) throws IOException {
        try (
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Subtask subtask = gson.fromJson(reader, Subtask.class);
            Integer idFromRequest = subtask.getId();
            if (subtask == null) {
                sendText(exchange, "Ошибка данных подзадачи.", 400);
            } else if (taskManager.isTaskOverlapping(subtask)) {
                sendHasOverlapping(exchange);
            } else if (idFromRequest == null) {
                taskManager.addSubtask(subtask);
                sendText(exchange, "Подзадача успешно добавлена.", 201);
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, "Подзадача успешно обновлена.", 201);
            }
        }
    }

    @Override
    public void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        if ("/subtasks".equals(path)) {
            taskManager.removeSubtasks();
            sendText(exchange, "Все подзадачи удалены.", 200);
        } else {
            int id = extractIdFromPath(path);
            taskManager.removeSubtaskByID(id);
            sendText(exchange, "Подзадача удалена.", 200);
        }
    }
}
