package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(history::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.keySet().forEach(history::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
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
        calculateFields(epic);
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
            calculateFields(epic);
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
            tasks.put(task.getId(), task);
            if (task.hasTimeChanged(oldTask)) {
                if (isOverlappingWithAny(task, oldTask)) {
                    throw new IllegalArgumentException("Нельзя обновить задачу - пересекается по времени " +
                            "с другой задачей.");
                }
                prioritizedTasks.remove(oldTask);
                updatePrioritizedTasks(task);
            }
        } else {
            System.out.println("Задачи с ID " + task.getId() + " не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            if (subtask.hasTimeChanged(oldSubtask)) {
                if (isOverlappingWithAny(subtask, oldSubtask)) {
                    throw new IllegalArgumentException("Нельзя обновить подзадачу - пересекается по времени " +
                            "с другой задачей.");
                }
                prioritizedTasks.remove(oldSubtask);
                updatePrioritizedTasks(subtask);
            }
        } else {
            System.out.println("Сабтаска с ID " + subtask.getId() + " не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
            epics.put(epic.getId(), epic);
            calculateFields(epic);
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
            calculateFields(getEpicById(epicId));
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

    public void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            if (epic != null) {
                epic.setStatus(Status.NEW);
            } else {
                throw new IllegalArgumentException("Эпик с ID " + id + " не найден.");
            }
            return;
        }

        List<Subtask> subtasksList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        if (subtasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        // Подсчитываем количество подзадач в каждом статусе
        Map<Status, Long> statusCounts = subtasksList.stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.counting()
                ));

        long statusDoneCount = statusCounts.getOrDefault(Status.DONE, 0L);
        long statusNewCount = statusCounts.getOrDefault(Status.NEW, 0L);
        long statusInProgressCount = statusCounts.getOrDefault(Status.IN_PROGRESS, 0L);

        int totalSubtasks = subtasksList.size();

        if (statusDoneCount == totalSubtasks) {
            epic.setStatus(Status.DONE);
        } else if (statusNewCount == totalSubtasks) {
            epic.setStatus(Status.NEW);
        } else if (statusInProgressCount == totalSubtasks) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.IN_PROGRESS); // Если есть подзадачи в разных статусах
        }
    }

    public void calculateFields(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Эпик для расчета полей не найден.");
        }
        if (epic.getSubtaskIds().isEmpty()) {
            epic.calculatedDuration = Duration.ZERO;
            epic.calculatedStartTime = null;
            epic.calculatedEndTime = null;
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                    latestEnd = subtaskEnd;
                }
            }
        }

        epic.calculatedDuration = totalDuration;
        epic.calculatedStartTime = earliestStart;
        epic.calculatedEndTime = latestEnd;
    }

    private int generateId() {
        return ++id;
    }

    // Метод для проверки пересечений через обход всех отсортированных задач
    private boolean isOverlappingWithAny(Task newTask) {
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();
        if (newStart == null || newEnd == null) {
            return false; // Если у задачи нет startTime или endTime, она не может пересекаться
        }
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isOverlapping(
                        existingTask.getStartTime(),
                        existingTask.getEndTime(),
                        newStart,
                        newEnd
                ));
    }

    private boolean isOverlappingWithAny(Task newTask, Task oldTask) {
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();
        if (newStart == null || newEnd == null) {
            return false; // Если у задачи нет startTime или endTime, она не может пересекаться
        }
        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask != oldTask)
                .anyMatch(existingTask -> isOverlapping(
                        existingTask.getStartTime(),
                        existingTask.getEndTime(),
                        newStart,
                        newEnd
                ));
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private void updatePrioritizedTasks(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        if (startTime == null || endTime == null) {
            prioritizedTasks.remove(task);
        } else {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }
    }
}