package ru.yandex.tasktracker.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.tasktracker.api.HttpTaskServer;
import ru.yandex.tasktracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHttpHandlerTest {
    TaskManager taskManager = Managers.getDefaultTaskManager();
    HistoryManager historyManager = Managers.getDefaultHistory();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager, historyManager);
    Gson gson = HttpTaskServer.getGson();

    PrioritizedHttpHandlerTest() throws IOException {
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
    public void getPrioritizedTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(15);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Testing task 2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(15);
        taskManager.addTask(task2);
        Set<Task> sortedTasksFromManager = taskManager.getPrioritizedTasks();
        String sortedTasksJson = gson.toJson(sortedTasksFromManager);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(sortedTasksJson, response.body());
    }
}