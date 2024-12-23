package ru.yandex.tasktracker.exceptions;

public class ManagerSaveException extends RuntimeException {

    private final String message = "Input error";

    public ManagerSaveException(String message) {
        super(message);
    }
}
