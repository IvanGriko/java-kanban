package ru.yandex.tasktracker.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {
    private final HistoryManager historyManager;
    private final Gson gson = new Gson();

    public HistoryHttpHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGetHistory(exchange);
        } else {
            sendText(exchange, "Ошибка: HTTP-метод не поддерживается.", 405);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> historyList = historyManager.getHistory();
        if (historyList.isEmpty()) {
            sendText(exchange, "История пуста.", 404);
        } else {
            sendText(exchange, gson.toJson(historyList), 200);
        }
    }
}
