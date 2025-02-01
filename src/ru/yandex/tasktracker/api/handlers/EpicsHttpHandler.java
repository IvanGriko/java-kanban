package ru.yandex.tasktracker.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicsHttpHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public EpicsHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if ("GET".equalsIgnoreCase(method)) {
            if (path.matches("/epics")) {
                handleGetEpics(exchange);
            } else if (path.matches("/epics/\\d+")) {
                handleGetEpicById(exchange);
            } else if (path.matches("/epics/\\d+/subtasks")) {
                handleGetSubtasksByEpic(exchange);
            } else {
                sendText(exchange, "Эндпоинт не найден.", 404);
            }
        } else if ("POST".equalsIgnoreCase(method) && "/epics".equals(path)) {
            handleAddOrUpdateEpic(exchange);
        } else if ("DELETE".equalsIgnoreCase(method) && path.matches("/epics/\\d+")) {
            handleRemoveEpic(exchange);
        } else {
            sendText(exchange, "Метод или эндпоинт не поддерживаются.", 405);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getEpics();
        if (epics.isEmpty()) {
            sendText(exchange, "Список эпиков пуст.", 404);
        } else {
            sendText(exchange, gson.toJson(epics), 200);
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            int id = extractIdFromPath(exchange.getRequestURI().getPath());
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                sendText(exchange, "Эпик не найден.", 404);
                return;
            }
            sendText(exchange, gson.toJson(epic), 200);
        } catch (NumberFormatException e) {
            sendText(exchange, "Ошибка числового ввода", 400);
        }
    }

    private void handleGetSubtasksByEpic(HttpExchange exchange) throws IOException {
        try {
            int id = extractIdFromPath(exchange.getRequestURI().getPath());
            List<Subtask> subtasks = taskManager.getSubtasksByEpicId(id);
            if (subtasks == null || subtasks.isEmpty()) {
                sendText(exchange, "Подзадачи для эпика с ID " + id + " не найдены.", 404);
                return;
            }
            sendText(exchange, gson.toJson(subtasks), 200);
        } catch (NumberFormatException e) {
            sendText(exchange, "Ошибка числового ввода", 400);
        }
    }

    private void handleAddOrUpdateEpic(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(requestBody, Epic.class);
        Integer idFromRequest = epic.getId();
        if (idFromRequest == null) {
            taskManager.addEpic(epic);
            sendText(exchange, "Эпик успешно добавлен.", 201);
        } else {
            taskManager.updateEpic(epic);
            sendText(exchange, "Эпик успешно обновлен.", 201);
        }
    }

    private void handleRemoveEpic(HttpExchange exchange) throws IOException {
        try {
            int id = extractIdFromPath(exchange.getRequestURI().getPath());
            taskManager.removeEpicById(id);
            sendText(exchange, "Эпик удалён.", 200);
        } catch (NumberFormatException e) {
            sendText(exchange, "Ошибка числового ввода", 400);
        }
    }
}
