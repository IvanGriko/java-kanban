import java.util.*;

public class Manager {

    int taskCount = 1;                  // счётчик задач

    HashMap<Integer, Task> tasksMap = new HashMap<>();
    HashMap<Integer, Subtask> subtasksMap = new HashMap<>();
    HashMap<Integer, Epic> epicsMap = new HashMap<>();

    // получение списка задач
    public ArrayList getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>(tasksMap.values());
        return tasksList;
    }

    // удаление всех задач
    public void deleteTasks(){
        tasksMap.clear();
    }

    // получение задачи по ID
    public Task getTask(int id){
        Task task = tasksMap.get(id);
        return task;
    }

    // добавление задачи
    public void addTask(Task task){
        tasksMap.put(task.getId(), task);
        taskCount++;
    }

    // обновление задачи
    public void updateTask(Task task){
        tasksMap.replace(task.getId(), task);
    }

    // удаление задачи
    public void removeTask(Task task){
        tasksMap.remove(task.getId());
    }

    // получение списка подзадач
    public ArrayList getSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
        return subtasksList;
    }

    // удаление всех подзадач
    public void deleteSubtasks(){
        subtasksMap.clear();
    }

    // получение подзадачи по ID
    Subtask getSubtask(int id){
        Subtask subtask = subtasksMap.get(id);
        return subtask;
    }

    // добавление подзадачи
    public void addSubtask(Subtask subtask){
        subtasksMap.put(subtask.getId(), subtask);
        taskCount++;
        updateEpicStatus(getEpic(subtask.getEpic()));
    }

    // обновление подзадачи
    public void updateSubtask(Subtask subtask){
        subtasksMap.replace(subtask.getId(), subtask);
        updateEpicStatus(getEpic(subtask.getEpic()));
    }

    // удаление подзадачи
    public void removeSubtask(Subtask subtask){
        subtasksMap.remove(subtask.getId());
        updateEpicStatus(getEpic(subtask.getEpic()));
    }

    // получение списка эпиков
    public ArrayList getEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>(epicsMap.values());
        return epicsList;
    }

    // удаление всех эпиков
    public void deleteEpics(){
        epicsMap.clear();
        subtasksMap.clear();
    }

    // получение эпика по ID
    Epic getEpic(int id){
        Epic epic = epicsMap.get(id);
        return epic;
    }

    // добавление эпика
    public void addEpic(Epic epic){
        epicsMap.put(epic.getId(), epic);
        taskCount++;
    }

    // обновление эпика
    public void updateEpic(Epic epic){
        epicsMap.replace(epic.getId(), epic);
    }

    // удаление эпика по ID
    public void removeEpic(int id){
        ArrayList<Subtask> epicSubtasks = getSubtasksByEpic(getEpic(id));
        for (Subtask subtask : epicSubtasks) {
            subtasksMap.remove(subtask.getId());
        }
        epicsMap.remove(id);
    }

    // обновление статуса эпика
    public void updateEpicStatus(Epic epic) {
        int isDoneCount = 0;
        int isInNewCount = 0;
        ArrayList<Subtask> list = getSubtasksByEpic(epic);

        for (Subtask subtask : list) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                isDoneCount++;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                isInNewCount++;
            }
        }
        if (isDoneCount == list.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (isInNewCount == list.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        updateEpic(epic);
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
        ArrayList<Subtask> subtasksListByEpic = new ArrayList<>();
        for (Subtask subt : subtasksList) {
            if ((epic.name).equals(subt.epic.name)) {
                subtasksListByEpic.add(subt);
            }
        }
        epic.setSubtaskList(subtasksList);
        return subtasksListByEpic;
    }
}
