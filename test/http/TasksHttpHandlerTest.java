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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksHttpHandlerTest {
    TaskManager taskManager = Managers.getDefaultTaskManager();
    HistoryManager historyManager = Managers.getDefaultHistory();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager, historyManager);
    Gson gson = HttpTaskServer.getGson();

    TasksHttpHandlerTest() throws IOException {
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
    public void addTaskTest() throws IOException, InterruptedException {
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
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Testing task 2");
        task2.setId(1);
        String taskJson2 = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void tasksAreOverlappingTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(15);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Testing task 2");
        task2.setStartTime(LocalDateTime.now());
        task2.setDuration(15);
        String taskJson2 = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }

    @Test
    public void getTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1");
        taskManager.addTask(task);
        Task task2 = new Task("Task 2", "Testing task 2");
        taskManager.addTask(task2);
        String getTasksJson = gson.toJson(taskManager.getTasks());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(getTasksJson, response.body());
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1");
        taskManager.addTask(task);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(taskJson, response.body());
    }

    @Test
    public void taskIsNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void removeTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1");
        taskManager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}