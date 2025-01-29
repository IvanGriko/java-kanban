package ru.yandex.tasktracker;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.tasktracker.api.HttpTaskServer;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.Managers;
import ru.yandex.tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        TaskManager taskManager = Managers.getDefaultTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistory();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager, historyManager);
        Gson gson = HttpTaskServer.getGson();

            taskManager.removeTasks();
            taskManager.removeSubtasks();
            taskManager.removeEpics();
            taskServer.start();

            Task task = new Task("Task 1", "Testing task 1");
            String taskJson = gson.toJson(task);
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
            List<Task> tasksFromManager = taskManager.getTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Task 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        taskServer.stop();
    }
}