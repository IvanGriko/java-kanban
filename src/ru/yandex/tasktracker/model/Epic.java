package ru.yandex.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class Epic extends Task {

    protected ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description, int taskCount) {
        super(name, description, taskCount);
        this.startTime = calculateStartTime();
        this.duration = calculateDuration();
        this.endTime = calculateEndTime();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void calculateTime() {
        calculateStartTime();
        calculateDuration();
        calculateEndTime();
    }

    public LocalDateTime calculateStartTime() {
        if (subtaskList == null || subtaskList.isEmpty()) {
            return LocalDateTime.MIN;
        }
        subtaskList
                .stream()
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .collect(Collectors.toList());
        startTime = subtaskList.getFirst().getStartTime();
        return startTime;
    }

    public LocalDateTime calculateEndTime() {
        if (subtaskList == null || subtaskList.isEmpty()) {
            return LocalDateTime.MAX;
        }
        subtaskList
                .stream()
                .sorted(Comparator.comparing(Subtask::getEndTime))
                .collect(Collectors.toList());
        endTime = subtaskList.getLast().endTime;
        return endTime;
    }

    public Duration calculateDuration() {
        if (subtaskList == null || subtaskList.isEmpty()) {
            return Duration.ZERO;
        }
        Duration duration = Duration.ZERO;
        for (Subtask subtask : subtaskList) {
            duration = duration.plus(subtask.getDuration());
        }
        return duration;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + ",EPIC," + name + "," + getStatus() + "," + description + "," + startTime + "," +
                getDurationMinutes() + "," + endTime + "\n";
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