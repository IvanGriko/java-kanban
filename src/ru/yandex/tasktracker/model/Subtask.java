package ru.yandex.tasktracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    protected Epic epic;

    public Subtask(String name, String description, int taskCount, TaskStatus status, Epic epic) {
        super(name, description, taskCount, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, int id, TaskStatus status, long durationMinutes, Epic epic) {
        super(name, description, id, status, durationMinutes);
        this.epic = epic;
    }

    public Subtask(String name, String description, int id, TaskStatus status, LocalDateTime startTime,
                   long durationMinutes, Epic epic) {
        super(name, description, id, status, startTime, durationMinutes);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return id + ",SUBTASK," + name + "," + getStatus() + "," + description + "," + getEpicId() + "," +
                startTime + "," + getDurationMinutes() + "," + endTime + "\n";
    }

    public int getEpicId() {
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