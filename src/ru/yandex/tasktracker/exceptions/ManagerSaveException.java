package ru.yandex.tasktracker.exceptions;

public class ManagerSaveException extends Exception {

    private final String message = "Input error";

    public ManagerSaveException(String message) {
        super(message);
    }
}
