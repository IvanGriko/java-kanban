package ru.yandex.tasktracker.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TasksHttpHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            switch (method) {
                case "GET":
                    if ("/tasks".equals(path)) {
                        sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
                    } else {
                        int id = extractIdFromPath(path);
                        Task task = taskManager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(task), 200);
                        }
                    }
                    break;
                case "POST":
                    try (
                            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                        Task task = gson.fromJson(reader, Task.class);
                        Integer idFromRequest = task.getId();
                        if (task == null) {
                            sendText(exchange, "Ошибка данных задачи.", 400);
                        } else if (taskManager.isTaskOverlapping(task)) {
                            sendHasOverlapping(exchange);
                        } else if (idFromRequest == null) {
                            taskManager.addTask(task);
                            sendText(exchange, "Задача успешно добавлена.", 201);
                        } else {
                            taskManager.updateTask(task);
                            sendText(exchange, "Задача успешно обновлена.", 201);
                        }
                    }
                    break;
                case "DELETE":
                    if ("/tasks".equals(path)) {
                        taskManager.removeTasks();
                        sendText(exchange, "Все задачи удалены.", 200);
                    } else {
                        int id = extractIdFromPath(path);
                        taskManager.removeTaskByID(id);
                        sendText(exchange, "Задача удалена.", 200);
                    }
                    break;
                default:
                    sendText(exchange, "Ошибка: HTTP-метод не поддерживается.", 405);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Ошибка числового значения.", 400);
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}
