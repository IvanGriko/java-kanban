package ru.yandex.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;

    public void setId(int id) {
        this.id = id;
    }

    protected int id;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, int id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(30);
        this.endTime = startTime.plus(duration);
    }

    public Task(String name, String description, int id, TaskStatus status, long durationMinutes) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(durationMinutes);
        this.endTime = startTime.plus(duration);
    }

    public Task(String name, String description, int id, TaskStatus status, LocalDateTime startTime, long durationMinutes) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinutes);
        this.endTime = startTime.plus(duration);
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, long durationMinutes) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinutes);
        this.endTime = startTime.plus(duration);
    }

    public void setDuration(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public long getDurationMinutes() {
        return duration.toMinutes();
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void setStatus(TaskStatus status) {
        switch (status) {
            case TaskStatus.NEW:
                this.status = TaskStatus.NEW;
                break;
            case TaskStatus.IN_PROGRESS:
                this.status = TaskStatus.IN_PROGRESS;
                break;
            case TaskStatus.DONE:
                this.status = TaskStatus.DONE;
                break;
            default:
                System.out.println("Такого статуса нет");
        }
    }

    public String toString() {
        return id + ",TASK," + name + "," + getStatus() + "," + description + "," + startTime + "," +
                getDurationMinutes() + "," + endTime + "\n";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
