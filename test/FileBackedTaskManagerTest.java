package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File testFile;
    private FileBackedTaskManager testManager;

    @BeforeEach
    void start() throws IOException {
        testFile = File.createTempFile("data",".csv");
        testManager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void end() throws ManagerSaveException {
        testManager.removeTasks();
        testManager.removeSubtasks();
        testManager.removeEpics();
    }

    @Test
    public void loadFromFileWhenFileIsEmpty() throws ManagerSaveException {
        testManager = testManager.loadFromFile(testFile);
        assertEquals(0, testManager.getTasks().size());
        assertEquals(0, testManager.getSubtasks().size());
        assertEquals(0, testManager.getEpics().size());
        assertEquals(0, testManager.getHistory().size());
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileCannotBeRead() {
        File nonExistentFile = new File("nonexistentfile.csv");
        Exception exception = Assertions.assertThrows(ManagerSaveException.class,
                () -> testManager.loadFromFile(nonExistentFile)
        );
        Assertions.assertTrue(exception.getMessage().contains("Ошибка чтения файла"),
                "Expected ManagerSaveException to be thrown with the correct message.");
    }
}