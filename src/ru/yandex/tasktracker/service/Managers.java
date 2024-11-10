package ru.yandex.tasktracker.service;

public class Managers {


    public Object taskManager;

    public Managers(InMemoryTaskManager taskManager) {
        this.taskManager = getInMemoryTaskManager();
    }

    public Managers() {
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
