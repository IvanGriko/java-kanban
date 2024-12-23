package ru.yandex.tasktracker.model;

import java.util.Objects;

public class Subtask extends Task {
    protected Epic epic;

    public Subtask(String name, String description, int taskCount, TaskStatus status, Epic epic) {
        super(name, description, taskCount, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return  "\n" + "Подзадача. Название: " + name + ", Описание: " + description +
                ", Статус: " + getStatus() + ", id " + id;
    }

    public int getEpic() {
        return this.epic.getId();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        if (Objects.equals(this.getId(), subtask.getId())) return true;
        return Objects.equals(epic, subtask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }
}