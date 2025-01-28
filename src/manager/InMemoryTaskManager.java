package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    // 1. Возможность хранить задачи всех типов.
    private final HistoryManager history = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int id = 0;
    protected final TreeSet<Task> prioritizedTasks =
            new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(Task::getId));


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
        tasks.keySet().forEach(history::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(history::remove);
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.keySet().forEach(history::remove);
        subtasks.clear();
        epics.keySet().forEach(history::remove);
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
        if (isOverlappingWithAny(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.calculateFields();
        updatePrioritizedTasks(epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            if (isOverlappingWithAny(subtask)) {
                throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
            }
            subtask.setId(generateId());
            subtask.setEpicId(id);
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            epic.calculateFields();
            updatePrioritizedTasks(subtask);
            updateEpicStatus(id);
        } else {
            System.out.println("Эпик с ID " + id + " не существует");
        }
    }

    // e. Обновление.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            if (isOverlappingWithAny(task, oldTask)) {
                throw new IllegalArgumentException("Обновленная задача пересекается по времени с другой задачей.");
            }
            tasks.put(task.getId(), task);
            updatePrioritizedTasks(oldTask);
            updatePrioritizedTasks(task);
        } else {
            System.out.println("Задачи с ID " + task.getId() + " не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            if (isOverlappingWithAny(subtask, oldSubtask)) {
                throw new IllegalArgumentException("Обновленная подзадача пересекается по времени с другой задачей.");
            }
            subtasks.put(subtask.getId(), subtask);
            updatePrioritizedTasks(oldSubtask);
            updatePrioritizedTasks(subtask);
            updateEpicStatus(subtask.getEpicId());
        } else {
            System.out.println("Сабтаска с ID " + subtask.getId() + " не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
            epics.put(epic.getId(), epic);
            updatePrioritizedTasks(oldEpic);
            updatePrioritizedTasks(epic);
        } else {
            System.out.println("Эпика с ID " + epic.getId() + " не существует");
        }
    }

    // f. Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            history.remove(id);
        } else {
            System.out.println("Задачи с ID " + id + " не существует");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
            subTaskIds.forEach(subtaskId -> {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
                subtasks.remove(subtaskId);
                history.remove(subtaskId);
            });
            prioritizedTasks.remove(epics.get(id));
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
            getEpicById(epicId).calculateFields();
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            history.remove(id);
            updateEpicStatus(epicId);
        } else {
            System.out.println("Сабтаска с ID " + id + " не существует");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return new ArrayList<>(); // Если эпик не найден, возвращаем пустой список
        }

        List<Subtask> subtasksList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull) // Фильтруем null значения, если задача не найдена
                .toList();

        return new ArrayList<>(subtasksList); // Преобразуем List в ArrayList
    }

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private int generateId() {
        return ++id;
    }

    private boolean isOverlappingWithAny(Task newTask) {
        LocalDateTime start = newTask.getStartTime();
        LocalDateTime end = newTask.getEndTime();

        if (start == null || end == null) {
            return false; // Если у задачи нет startTime или endTime, она не может пересекаться
        }

        return prioritizedTasks.stream()
                .anyMatch(existingTask -> existingTask.getStartTime().isBefore(end) &&
                        existingTask.getEndTime().isAfter(start) &&
                        newTask.isOverlapping(existingTask));
    }

    private boolean isOverlappingWithAny(Task newTask, Task oldTask) {
        LocalDateTime start = newTask.getStartTime();
        LocalDateTime end = newTask.getEndTime();

        if (start == null || end == null) {
            return false; // Если у задачи нет startTime или endTime, она не может пересекаться
        }

        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask != oldTask)
                .anyMatch(existingTask -> existingTask.getStartTime().isBefore(end) &&
                        existingTask.getEndTime().isAfter(start) &&
                        newTask.isOverlapping(existingTask));
    }

    private void updatePrioritizedTasks(Task task) {
        if (task.getStartTime() == null) {
            prioritizedTasks.remove(task);
        } else {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            assert epic != null;
            epic.setStatus(Status.NEW);
            return;
        }

        long statusDoneCount = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .filter(subtask -> subtask.getStatus() == Status.DONE)
                .count();

        long statusNewCount = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .filter(subtask -> subtask.getStatus() == Status.NEW)
                .count();

        int totalSubtasks = epic.getSubtaskIds().size();

        if (statusDoneCount == totalSubtasks) {
            epic.setStatus(Status.DONE);
        } else if (statusNewCount == totalSubtasks) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}