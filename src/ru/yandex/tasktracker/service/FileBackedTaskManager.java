package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.TaskStatus;
import ru.yandex.tasktracker.model.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static int taskCount;
    private final File file;
    private static final Map<Integer, Task> tasksMap = new HashMap<>();
    private static final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private static final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        if (file != null) {
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.append("id,type,name,status,description,epic\n");
                for (Task task : getTasks()) {
                    writer.write(taskToString(task) + "\n");
                }
                for (Subtask task : getSubtasks()) {
                    writer.write(taskToString(task) + "\n");
                }
                for (Epic task : getEpics()) {
                    writer.write(taskToString(task) + "\n");
                }
                writer.append("История запросов: \n");
                for (Task historyTask : getHistory()) {
                    Integer id1 = historyTask.getId();
                    writer.append(id1 + ", \n");
                }
            } catch (IOException e) {
                throw new ManagerSaveException();
            }
        }
    }

    public FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);
        List<Task> backedTasksList = new ArrayList<>();
        int backedId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                Task task = taskFromString(line);
                backedTasksList.add(task);
                final int id = task.getId();
                if (task.getType(task) == TaskType.TASK) {
                    tasksMap.put(id, task);
                    if (backedId < id) {
                        backedId = id;
                    }
                } else if (task.getType(task) == TaskType.EPIC) {
                    epicsMap.put(id, (Epic) task);
                    if (backedId < id) {
                        backedId = id;
                    }
                } else {
                    subtasksMap.put(id, (Subtask) task);
                    if (backedId < id) {
                        backedId = id;
                    }
                }
                if (line.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        taskCount = backedId;
        return backedTaskManager;
    }

    private String taskToString(Task task) throws ManagerSaveException {
        TaskType type;
        if (getTasks().contains(task.getId())) {
            type = TaskType.TASK;
            return String.format("%d,%S,%s,%S,%s", task.getId(), type, task.getName(), task.getStatus(), task.getDescription());
        } else if (getSubtasks().contains(task.getId())) {
            type = TaskType.SUBTASK;
            int epicId = getSubtask(task.getId()).getEpic();
            return String.format("%d,%S,%s,%S,%s,%d", task.getId(), type, task.getName(), task.getStatus(), task.getDescription(), epicId);
        } else if (getEpics().contains(task.getId())){
            type = TaskType.EPIC;
            return String.format("%d,%S,%s,%S,%s", task.getId(), type, task.getName(), task.getStatus(), task.getDescription());
        }
        return null;
    }

    private Task taskFromString(String value) throws ManagerSaveException {
        Task task = null;
        final String[] str = value.split(",");
        TaskType type = TaskType.valueOf(str[1]);
        switch (type) {
            case TASK:
                int id = Integer.parseInt(str[0]);
                String name = str[2];
                String description = str[3];
                TaskStatus status = TaskStatus.valueOf(str[4]);
                task = new Task(name, description, id, status);
                break;
            case SUBTASK:
                id = Integer.parseInt(str[0]);
                name = str[2];
                description = str[3];
                status = TaskStatus.valueOf(str[4]);
                int epicId = Integer.parseInt(str[5]);
                task = new Subtask(name, description, id, status, getEpic(epicId));
                break;
            case EPIC:
                id = Integer.parseInt(str[0]);
                name = str[2];
                description = str[3];
                status = TaskStatus.valueOf(str[4]);
                task = new Epic(name, description, id);
                task.setStatus(status);
                break;
        }
        return task;
    }

    @Override
    public List<Task> getTasks() throws ManagerSaveException {
        List<Task> taskList = super.getTasks();
        save();
        return taskList;
    }

    @Override
    public void deleteTasks() throws ManagerSaveException {
        super.deleteTasks();
        save();
    }

    @Override
    public int getTaskCount() {
        return super.getTaskCount();
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void addTask(Task task) throws ManagerSaveException {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(Task task) throws ManagerSaveException {
        super.removeTask(task);
        save();
    }

    @Override
    public void removeTaskByID(int id) throws ManagerSaveException {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasks() throws ManagerSaveException {
        List<Subtask> subtaskList = super.getSubtasks();
        save();
        return subtaskList;
    }

    @Override
    public void deleteSubtasks() throws ManagerSaveException {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Subtask getSubtask(int id) throws ManagerSaveException {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void addSubtask(Subtask subtask) throws ManagerSaveException {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(Subtask subtask) throws ManagerSaveException {
        super.removeSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskByID(int id) throws ManagerSaveException {
        super.removeSubtaskByID(id);
        save();
    }

    @Override
    public List<Epic> getEpics() throws ManagerSaveException {
        List<Epic> epicsList = super.getEpics();
        save();
        return epicsList;
    }

    @Override
    public void deleteEpics() throws ManagerSaveException {
        super.deleteEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) throws ManagerSaveException {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void addEpic(Epic epic) throws ManagerSaveException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) throws ManagerSaveException {
        super.removeEpic(id);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) throws ManagerSaveException {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) throws ManagerSaveException {
        List<Subtask> subtasksListByEpic = super.getSubtasksByEpic(epic);
        save();
        return subtasksListByEpic;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
