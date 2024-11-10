package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Task;
import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    ArrayList getHistory();

}
