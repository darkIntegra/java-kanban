package manager;

import exception.ManagerValidatePriorityException;
import exception.NotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
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
    public Task getTaskById(int id) throws NotFoundException {
        if (!tasks.containsKey(id)) {
            throw new NotFoundException("Задача с ID " + id + " не найдена.");
        }
        Task task = tasks.get(id);
        history.add(task); // Добавляем задачу в историю
        return task;
    }

    @Override
    public Epic getEpicById(int id) throws NotFoundException {
        if (!epics.containsKey(id)) {
            throw new NotFoundException("Эпик с ID " + id + " не найден.");
        }
        Epic epic = epics.get(id);
        history.add(epic); // Добавляем эпик в историю
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) throws NotFoundException {
        if (!subtasks.containsKey(id)) {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена.");
        }
        Subtask subtask = subtasks.get(id);
        history.add(subtask); // Добавляем подзадачу в историю
        return subtask;
    }

    // d. Создание.
    @Override
    public void createTask(Task task) {
        if (isOverlappingWithAny(task)) {
            throw new ManagerValidatePriorityException("Задача пересекается по времени с другой задачей.");
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
                throw new ManagerValidatePriorityException("Задача пересекается по времени с другой задачей.");
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
                    throw new ManagerValidatePriorityException("Нельзя обновить задачу - пересекается по времени " +
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
                    throw new ManagerValidatePriorityException("Нельзя обновить подзадачу - пересекается по времени " +
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
            calculateFields(epics.get(epicId));
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    public boolean containsTask(int id) {
        return tasks.containsKey(id);
    }

    public boolean containsSubtask(int id) {
        return subtasks.containsKey(id);
    }

    @Override
    public boolean containsEpic(int id) {
        return epics.containsKey(id);
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new ManagerValidatePriorityException("Эпик с ID " + epicId + " не найден.");
        }

        List<Integer> subtaskIds = epic.getSubtaskIds();
        List<Subtask> subtaskList = subtaskIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        epic.updateStatus(subtaskList); // Передаем список подзадач в метод updateStatus
    }

    protected void calculateFields(Epic epic) {
        if (epic == null) {
            throw new ManagerValidatePriorityException("Эпик для расчета полей не найден.");
        }

        // Если у эпика нет подзадач, сохраняем оригинальные значения startTime и duration
        if (epic.getSubtaskIds() == null || epic.getSubtaskIds().isEmpty()) {
            if (epic.getStartTime() == null || epic.getDuration() == null) {
                epic.setStartTime(null);
                epic.setEndTime(null);
                epic.setDuration(Duration.ZERO);
            } else {
                epic.setStartTime(epic.getStartTime()); // Сохраняем оригинальное время начала
                epic.setEndTime(epic.getStartTime().plus(epic.getDuration())); // Рассчитываем конец на основе duration
                epic.setDuration(epic.getDuration()); // Сохраняем оригинальную duration
            }
            return;
        }

        // Инициализация переменных для расчета
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        // Проходим по всем подзадачам эпика
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            // Игнорируем подзадачи с неполными временными данными
            if (subtask == null || subtask.getStartTime() == null || subtask.getDuration() == null) {
                continue;
            }
            // Обновляем общую продолжительность
            totalDuration = totalDuration.plus(subtask.getDuration());
            // Находим самое раннее время начала
            if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                earliestStart = subtask.getStartTime();
            }
            // Находим самое позднее время окончания
            LocalDateTime subtaskEnd = subtask.getEndTime(); // endTime вычисляется как startTime + duration
            if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                latestEnd = subtaskEnd;
            }
        }

        // Устанавливаем рассчитанные значения в эпик
        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
        epic.setDuration(totalDuration);
    }

    private int generateId() {
        return ++id;
    }

    // Метод для проверки пересечений через обход всех отсортированных задач
    protected boolean isOverlappingWithAny(Task newTask) {
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

    protected void updatePrioritizedTasks(Task task) {
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