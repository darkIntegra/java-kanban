package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    // 1. Возможность хранить задачи всех типов.
    HistoryManager history = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final ArrayList<Task> historyList = history.getHistory();

    private int id = 0;

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
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
        }
        epics.clear();
    }

    // c. Получение по идентификатору.
    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task с " + id + " не существует");
        }
        if (historyList.size() == 10) {
            historyList.removeFirst();
        }
        historyList.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Epic с " + id + " не существует");
        }
        if (historyList.size() == 10) {
            historyList.removeFirst();
        }
        historyList.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Subtask с " + id + " не существует");
        }
        if (historyList.size() == 10) {
            historyList.removeFirst();
        }
        historyList.add(subtasks.get(id));
        return subtasks.get(id);
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
        //проверка на содержание id в мапе эпиков
        if (epics.containsKey(id)) {
            //если выполняется то в полученный аргумент "сабтаск" посылаем новый ид
            subtask.setId(generateId());
            //совмещаем сабтаск с эпиком
            subtask.setEpicId(id);
            //добавляем в мапу полученный сабтаск
            subtasks.put(subtask.getId(), subtask);
            //добавляем в эпик список ид сабтасков
            epics.get(id).addSubtaskId(subtask.getId());
            //сохраняем эпик с списком сабтасков в эпике
            epics.put(id, epics.get(id));
            //обновляем статус
            updateEpicStatus(id);
        } else {
            System.out.println("Такого Tasks.Epic не существует");
        }
    }

    // e. Обновление.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
        epics.put(epic.getId(), epic);
    }

    // f. Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subTaskIds = epics.get(id).getSubtaskIds();
            for (Integer subtaskId : subTaskIds) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Эпика с " + id + " id не существует");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(subtasks.get(id).getEpicId()).deleteSubtaskIds(id);
        subtasks.remove(id);
        updateEpicStatus(epicId);
    }

    @Override
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

    public ArrayList<Task> getHistory() {
        return historyList;
    }

    private int generateId() {
        return ++id;
    }

    private void updateEpicStatus(int id) {
        //проверяем что список подзадач в эпике пуст
        if (epics.get(id).getSubtaskIds().isEmpty()) {
            //если да то в эпик посылаем статус new
            epics.get(id).setStatus(Status.NEW);
        } else {
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
    }
}