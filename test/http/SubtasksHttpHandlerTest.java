package ru.yandex.tasktracker.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.tasktracker.api.HttpTaskServer;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHttpHandlerTest {
    TaskManager taskManager = Managers.getDefaultTaskManager();
    HistoryManager historyManager = Managers.getDefaultHistory();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager, historyManager);
    Gson gson = HttpTaskServer.getGson();

    SubtasksHttpHandlerTest() throws IOException {
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
    public void addSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Testing subtask",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = taskManager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                2, TaskStatus.NEW, LocalDateTime.now().plusMinutes(20), 15, epic);
        String subtaskJson2 = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = taskManager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подззадач");
        assertEquals("Subtask 2", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void subtasksAreOverlappingTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                3, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        String subtaskJson2 = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void getSubtasksTest()  throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                3, TaskStatus.NEW, LocalDateTime.now().plusMinutes(20), 15, epic);
        taskManager.addSubtask(subtask2);
        String getSubtasksJson = gson.toJson(taskManager.getSubtasks());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(getSubtasksJson, response.body());
    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Testing subtask",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        taskManager.addSubtask(subtask);
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(subtaskJson, response.body());
    }

    @Test
    public void subtaskIsNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void removeSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Testing subtask",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        taskManager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
