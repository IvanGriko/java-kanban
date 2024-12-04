package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node> historyMap = new HashMap<>();

    private Node<Task> linkLast(Task task) {
        final Node<Task> newNode;
        final Node<Task> oldTail = tail;
        newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        Task t = newNode.task;
        historyMap.put(t.getId(), newNode);
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasksList = new LinkedList<>();
        Node<Task> node = head;
        while (node != null) {
            tasksList.add(node.task);
            node = node.next;
        }
        return tasksList;
    }

    private void removeNode(Node node) {
        if (node.next == null && node.prev == null) {
            head = null;
            tail = null;
            node.task = null;
        } else if (node == head) {
            head = node.next;
            node.task = null;
        } else if (node == tail) {
            tail = node.prev;
            node.task = null;
        } else if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(Integer id) {
        Node node = historyMap.get(id);
        if (node != null) {
            removeNode(node);
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
