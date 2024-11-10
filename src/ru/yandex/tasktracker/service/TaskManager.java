package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface TaskManager {

        Map<Integer, Task> tasksMap = new HashMap<>();
        Map<Integer, Subtask> subtasksMap = new HashMap<>();
        Map<Integer, Epic> epicsMap = new HashMap<>();

        // получение списка задач
        default ArrayList getTasks() {
            ArrayList<Task> tasksList = new ArrayList<>(tasksMap.values());
            return tasksList;
        }

        // удаление всех задач
        default void deleteTasks(){
            tasksMap.clear();
        }

        // получение задачи по ID
        default Task getTask(int id){
            Task task = tasksMap.get(id);
            return task;
        }

        // добавление задачи
        default void addTask(Task task){
            tasksMap.put(task.getId(), task);
        }

        // обновление задачи
        default void updateTask(Task task){
            tasksMap.replace(task.getId(), task);
        }

        // удаление задачи
        default void removeTask(Task task){
            tasksMap.remove(task.getId());
        }

        // удаление задачи по ID
        default void removeTaskByID(int id){
            tasksMap.remove(id);
        }

        // получение списка подзадач
        default ArrayList getSubtasks() {
            ArrayList<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
            return subtasksList;
        }

        // удаление всех подзадач
        default void deleteSubtasks(){
            subtasksMap.clear();
            for (Epic epic : epicsMap.values()) {
                epic.clearSubtasks();
                epic.setStatus(TaskStatus.NEW);
            }
        }

        // получение подзадачи по ID
        default Subtask getSubtask(int id){
            Subtask subtask = subtasksMap.get(id);
            return subtask;
        }

        // добавление подзадачи
        default void addSubtask(Subtask subtask){
            subtasksMap.put(subtask.getId(), subtask);
            updateEpicStatus(getEpic(subtask.getEpic()));
        }

        // обновление подзадачи
        default void updateSubtask(Subtask subtask){
            subtasksMap.replace(subtask.getId(), subtask);
            updateEpicStatus(getEpic(subtask.getEpic()));
        }

        // удаление подзадачи
        default void removeSubtask(Subtask subtask){
            subtasksMap.remove(subtask.getId());
            updateEpicStatus(getEpic(subtask.getEpic()));
        }

        // удаление подзадачи по ID
        default void removeSubtaskByID(int id){
            Subtask subtask = subtasksMap.get(id);
            int epicID = subtask.getEpic();
            subtasksMap.remove(id);
            Epic epic = epicsMap.get(epicID);
            ArrayList<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(subtask);
            epic.setSubtaskList(subtaskList);
            updateEpicStatus(epic);
        }

        // получение списка эпиков
        default ArrayList getEpics() {
            ArrayList<Epic> epicsList = new ArrayList<>(epicsMap.values());
            return epicsList;
        }

        // удаление всех эпиков
        default void deleteEpics(){
            epicsMap.clear();
            subtasksMap.clear();
        }

        // получение эпика по ID
        default Epic getEpic(int id){
            Epic epic = epicsMap.get(id);
            return epic;
        }

        // добавление эпика
        default void addEpic(Epic epic){
            epicsMap.put(epic.getId(), epic);
        }

        // обновление эпика
        default void updateEpic(Epic epic){
            epicsMap.replace(epic.getId(), epic);
        }

        // удаление эпика по ID
        default void removeEpic(int id){
            ArrayList<Subtask> epicSubtasks = getSubtasksByEpic(getEpic(id));
            for (Subtask subtask : epicSubtasks) {
                subtasksMap.remove(subtask.getId());
            }
            epicsMap.remove(id);
        }

        // обновление статуса эпика
        default void updateEpicStatus(Epic epic) {
            int isDoneCount = 0;
            int isNewCount = 0;
            ArrayList<Subtask> list = getSubtasksByEpic(epic);

            for (Subtask subtask : list) {
                if (subtask.getStatus() == TaskStatus.DONE) {
                    isDoneCount++;
                }
                if (subtask.getStatus() == TaskStatus.NEW) {
                    isNewCount++;
                }
            }
            if (isDoneCount == list.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (isNewCount == list.size()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
            updateEpic(epic);
        }

        // Получение списка всех подзадач определённого эпика
        default ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
            ArrayList<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
            ArrayList<Subtask> subtasksListByEpic = new ArrayList<>();
            for (Subtask subt : subtasksList) {
                if (epic.getId() == subt.getEpic()) {
                    subtasksListByEpic.add(subt);
                }
            }
            epic.setSubtaskList(subtasksListByEpic);
            return subtasksListByEpic;
        }

    default ArrayList getHistory() {
        return null;
    }
}
