package ru.yandex.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.service.FileBackedTaskManager;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FileBackedTaskManagerTest extends FileBackedTaskManager {

    File testFile;
    private FileBackedTaskManager taskManager;

    public FileBackedTaskManagerTest(File file) {
        super(file);
    }

    @BeforeEach
    void start() throws IOException {
        testFile = File.createTempFile("data",".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void end() throws ManagerSaveException {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }

    @Test
    public void loadFromFileWhenFileIsEmpty() throws IOException, ManagerSaveException {
        taskManager = taskManager.loadFromFile(testFile);
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getHistory().size());
    }
}