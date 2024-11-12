package ru.yandex.tasktracker.model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Subtask> subtaskList = new ArrayList<>();

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public String getName() {
        return name;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public Epic(String name, String description, int taskCount) {
        super(name, description, taskCount);
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nЭпик. Название: " + name + ", Описание: " + description +
                ", Статус: " + getStatus() + ", " + "id " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(this.getId(), epic.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskList);
    }
}