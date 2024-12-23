package ru.yandex.tasktracker.service;

import java.io.File;

public class Managers {

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getBackup(File file) {
        return new FileBackedTaskManager(file);
    }
}