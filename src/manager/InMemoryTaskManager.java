package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    // 1. Возможность хранить задачи всех типов.
    private final HistoryManager history = Managers.getDefaultHistory();

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected int id = 0;

    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // 2. Методы для каждого из типа задач:
    // a. Получение списка всех задач.

    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    // b. Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            history.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer taskId : subtasks.keySet()) {
            history.remove(taskId);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            history.remove(epicId);
        }
        subtasks.clear();
        epics.clear();
    }

    // c. Получение по идентификатору.
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        history.add(subtask);
        return subtask;
    }

    // d. Создание.
    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            subtask.setId(generateId());
            subtask.setEpicId(id);
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(id);
        } else {
            System.out.println("Эпик с ID " + id + " не существует");
        }
    }

    // e. Обновление.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задачи с ID " + task.getId() + " не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        } else {
            System.out.println("Сабтаска с ID " + subtask.getId() + " не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпика с ID " + epic.getId() + " не существует");
        }
    }

    // f. Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            history.remove(id);
        } else {
            System.out.println("Задачи с ID " + id + " не существует");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subTaskIds = epics.get(id).getSubtaskIds();
            for (Integer subtaskId : subTaskIds) {
                subtasks.remove(subtaskId);
                history.remove(subtaskId);
            }
            epics.remove(id);
            history.remove(id);
        } else {
            System.out.println("Эпика с ID " + id + " не существует");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            epics.get(epicId).deleteSubtaskIds(id);
            subtasks.remove(id);
            historyManager.remove(id);
            updateEpicStatus(epicId);
        } else {
            System.out.println("Сабтаска с ID " + id + " не существует");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    subtasksList.add(subtask);
                }
            }
        } else {
            System.out.println("Эпика с ID " + id + " не существует");
        }
        return subtasksList;
    }

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    private int generateId() {
        return ++id;
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            int statusNew = 0;
            int statusDone = 0;
            for (Integer subtaskId : epic.getSubtaskIds()) {
                Status subtaskStatus = subtasks.get(subtaskId).getStatus();
                if (subtaskStatus.equals(Status.DONE)) {
                    statusDone++;
                } else if (subtaskStatus.equals(Status.NEW)) {
                    statusNew++;
                }
            }
            if (statusDone == epic.getSubtaskIds().size()) {
                epic.setStatus(Status.DONE);
            } else if (statusNew == epic.getSubtaskIds().size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}