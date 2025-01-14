package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.TaskStatus;
import ru.yandex.tasktracker.model.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static int taskCount;
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager() {
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void save() throws ManagerSaveException {
        if (file == null) {
            return;
        }
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            for (Task task : getTasks()) {
                writer.write(task.toString());
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString());
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString());
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);
        int backedId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                Task task = taskFromString(line);
                final int id = task.getId();
                if (line.isEmpty()) {
                    break;
                } else if (task.getType().equals(TaskType.TASK)) {
                    backedTaskManager.addTask(task);
                    if (backedId < id) {
                        backedId = id;
                    }
                } else if (task.getType().equals(TaskType.EPIC)) {
                    backedTaskManager.addEpic((Epic) task);
                    if (backedId < id) {
                        backedId = id;
                    }
                } else {
                    backedTaskManager.addSubtask((Subtask) task);
                    if (backedId < id) {
                        backedId = id;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        taskCount = backedId;
        return backedTaskManager;
    }

    private Task taskFromString(String line) throws ManagerSaveException, IOException {
        Task task = null;
        String[] str = line.split(",");
        if ((str.length) > 1) {
            int id;
            String name;
            String description;
            TaskStatus status;
            switch (TaskType.valueOf(str[1])) {
                case TASK:
                    id = Integer.parseInt(str[0]);
                    name = str[2];
                    description = str[4];
                    status = TaskStatus.valueOf(str[3]);
                    task = new Task(name, description, id, status);
                    task.setStartTime(LocalDateTime.parse(str[5]));
                    task.setDuration(Long.parseLong(str[6]));
                    break;
                case SUBTASK:
                    id = Integer.parseInt(str[0]);
                    name = str[2];
                    description = str[4];
                    status = TaskStatus.valueOf(str[3]);
                    int epicId = Integer.parseInt(str[5]);
                    task = new Subtask(name, description, id, status, getEpicByIdWithoutMemorize(epicId));
                    task.setStartTime(LocalDateTime.parse(str[6]));
                    task.setDuration(Long.parseLong(str[7]));
                    break;
                case EPIC:
                    id = Integer.parseInt(str[0]);
                    name = str[2];
                    description = str[4];
                    status = TaskStatus.valueOf(str[3]);
                    task = new Epic(name, description, id);
                    task.setStatus(status);
                    task.setStartTime(LocalDateTime.parse(str[5]));
                    task.setDuration(Long.parseLong(str[6]));
                    break;
            }
        }
        return task;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(Task task) {
        super.removeTask(task);
        save();
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        super.removeSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskByID(int id) {
        super.removeSubtaskByID(id);
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public Epic getEpicByIdWithoutMemorize(int id) {
        Epic epic = super.getEpicByIdWithoutMemorize(id);
        save();
        return epic;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksListByEpic = super.getSubtasksByEpic(epic);
        save();
        return subtasksListByEpic;
    }
}
