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

class EpicsHttpHandlerTest {
    TaskManager taskManager = Managers.getDefaultTaskManager();
    HistoryManager historyManager = Managers.getDefaultHistory();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager, historyManager);
    Gson gson = HttpTaskServer.getGson();

    EpicsHttpHandlerTest() throws IOException {
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
    public void addEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = taskManager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void getEpicsTest()  throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", 1);
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", 2);
        taskManager.addEpic(epic2);
        String getEpicsJson = gson.toJson(taskManager.getEpics());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(getEpicsJson, response.body());
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(epicJson, response.body());
    }

    @Test
    public void epicIsNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void removeEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSubtasksByEpicIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 5);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1",
                2, TaskStatus.NEW, LocalDateTime.now(), 15, epic);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                3, TaskStatus.NEW, LocalDateTime.now().plusMinutes(20), 15, epic);
        taskManager.addSubtask(subtask2);
        String subtasksByEpicIdJson = gson.toJson(taskManager.getSubtasksByEpicId(5));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(subtasksByEpicIdJson, response.body());
    }

    @Test
    public void getSubtasksByEpicIdIsNotFoundTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic", 1);
        taskManager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}