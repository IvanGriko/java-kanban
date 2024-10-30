public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected final int id;

    public Task(String name, String description, int taskCount, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = taskCount;
        this.status = status;
    }

    public Task(String name, String description, int taskCount) {
        this.name = name;
        this.description = description;
        this.id = taskCount;
        this.status = TaskStatus.NEW;
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

    public void setStatus(TaskStatus status) {
        switch (status) {
            case NEW:
                this.status = TaskStatus.NEW;
                break;
            case IN_PROGRESS:
                this.status = TaskStatus.IN_PROGRESS;
                break;
            case DONE:
                this.status = TaskStatus.DONE;
                break;
            default:
                System.out.println("Такого статуса нет");
        }
    }

    public String toString() {
        return "\n" + "Задача. Название: " + name + ", Описание: " + description +
                ", Статус: " + getStatus() + ", id " + id;
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
