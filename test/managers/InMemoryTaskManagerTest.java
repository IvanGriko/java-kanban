package test;

import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public void initManager() {
        testManager = Managers.getDefaultTaskManager();
    }
}