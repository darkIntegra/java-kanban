import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    // 1. Возможность хранить задачи всех типов.
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int id = 0;

    private int generateId() {
        return ++id;
    }

    // 2. Методы для каждого из типа задач:
    // а. Получение списка всех задач.
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // b. Удаление всех задач.
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        } else {
            System.out.println("Список Tasks пуст");
        }
    }

    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            subtasks.clear();
            epics.clear();
        } else {
            System.out.println("Список Epics пуст");
        }
    }

    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        } else {
            System.out.println("Список Subtasks пуст");
        }
    }

    // c. Получение по идентификатору.
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task с " + id + " id не существует");
        }
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Epic с " + id + " id не существует");
        }
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Subtask с " + id + " id не существует");
        }
        return subtasks.get(id);
    }

    // d. Создание.
    public void addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(subtask.getEpicId());
        } else {
            System.out.println("Такого Epic не существует");
        }
    }

    // e. Обновление.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
        epic.setStatus(epics.get(epic.getId()).getStatus());
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus(subtask.getEpicId());
    }

    public void updateStatus(int id) {
        int statusNew = 0;
        int statusDone = 0;
        ArrayList<Integer> subtasksList = epics.get(id).getSubtaskIds();
        for (Integer taskId : subtasksList) {
            if (subtasks.get(taskId).getStatus().equals(Status.DONE)) {
                statusDone++;
            } else if (subtasks.get(taskId).getStatus().equals(Status.NEW)) {
                statusNew++;
            }
        }
        if (subtasksList.size() == statusDone || subtasksList.isEmpty()) {
            epics.get(id).setStatus(Status.DONE);
        } else if (subtasksList.size() == statusNew) {
            epics.get(id).setStatus(Status.NEW);
        } else {
            epics.get(id).setStatus(Status.IN_PROGRESS);
        }
    }

    // f. Удаление по идентификатору.
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subTaskIds = epics.get(id).getSubtaskIds();
            for (Integer subtaskID : subTaskIds) {
                subtasks.remove(subtaskID);
            }
            epics.remove(id);
        } else {
            System.out.println("Эпика с " + id + " id не существует");
        }
    }

    public void deleteSubTask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(subtasks.get(id).getEpicId()).deleteSubtaskIds(id);
            subtasks.remove(id);
            updateStatus(epicId);
        } else {
            System.out.println("Subtask с " + id + " id не существует");
        }
    }

    // 3. a. Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        //как получить id подзадачи? все подзадачи находятся в мапе subtasks(int, subTask) но мы не знаем ни key ни value
        Epic epic = epics.get(id);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        //перебираем список id подзадач, добавляя в конечный список данные из Мап с подзадачами по id
        for (Integer subtaskId : subtaskIds) {
            subtasksList.add(subtasks.get(subtaskId));
        }
        return subtasksList;
    }

}
