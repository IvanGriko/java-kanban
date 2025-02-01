package ru.yandex.tasktracker.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.tasktracker.api.HttpTaskServer;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHttpHandlerTest {
    TaskManager taskManager = Managers.getDefaultTaskManager();
    HistoryManager historyManager = Managers.getDefaultHistory();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager, historyManager);
    Gson gson = HttpTaskServer.getGson();

    HistoryHttpHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1",
                1, TaskStatus.NEW, LocalDateTime.now(), 15);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Testing task 2",
                2, TaskStatus.NEW, LocalDateTime.now().plusMinutes(20), 15);
        taskManager.addTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        List<Task> historyFromManager = taskManager.getHistory();
        String historyJson = gson.toJson(historyFromManager);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(historyJson, response.body());
    }
}