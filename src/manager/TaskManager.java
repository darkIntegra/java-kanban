package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;

public interface TaskManager {
    // 2. Методы для каждого из типа задач:
    // а. Получение списка всех задач.
    Collection<Task> getTasks();
    Collection<Epic> getEpics();
    Collection<Subtask> getSubtasks();

    // b. Удаление всех задач.
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();

    // c. Получение по идентификатору.
    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    // d. Создание.
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask, int id);

    // e. Обновление.
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    // f. Удаление по идентификатору.
    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);

    // 3. a. Получение списка всех подзадач определённого эпика.
    ArrayList<Subtask> getSubtasksByEpicId(int id);

    ArrayList<Task> getHistory();
}
