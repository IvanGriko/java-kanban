package ru.yandex.tasktracker.service;

public class Managers {

    public static FileBackedTaskManager getDefaultTaskManager() {
        return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}