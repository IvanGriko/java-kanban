package ru.yandex.tasktracker.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Found", 404);
    }

    protected void sendHasOverlapping(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Acceptable: Задача пересекается с другой задачей", 406);
    }

    protected void sendServerError(HttpExchange exchange) throws IOException {
        sendText(exchange, "Internal Server Error", 500);
    }

    protected int extractIdFromPath(String path) throws NumberFormatException {
        int indexOfSlash = path.indexOf('/');
        if (indexOfSlash != -1) {
            path = path.substring(indexOfSlash + 1);
        }
        return Integer.parseInt(path);
    }
}
