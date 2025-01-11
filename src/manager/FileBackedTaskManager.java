package manager;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksFile;

    //для работы с историей задач
    public FileBackedTaskManager(File tasksFile) {
        super(Managers.getDefaultHistory());
        this.tasksFile = tasksFile;
    }

    //подготовка для записи в файл
    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(
                ",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription()
        ));
        if (task.getType() == Type.SUBTASK) {
            sb.append(",").append(((Subtask) task).getEpicId());
        }
        return sb.toString();
    }

    //запись данных в файл
    public void save() {
        try {
            if (!Files.exists(tasksFile.toPath())) {
                Files.createFile(tasksFile.toPath());
            }
            try (Writer fileWriter = new FileWriter(tasksFile, StandardCharsets.UTF_8)) {
                fileWriter.write("id,type,name,status,description,epic\n");
                for (Task task : getTasks()) {
                    fileWriter.write(taskToString(task) + "\n");
                }
                for (Epic epic : getEpics()) {
                    fileWriter.write(taskToString(epic) + "\n");
                }
                for (Subtask subtask : getSubtasks()) {
                    fileWriter.write(taskToString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    //подготовка к считыванию из файла
    private Task taskFromString(String str) {
        Task task = null;
        String[] taskFields = str.split(",");
        Status status = switch (taskFields[3]) {
            case "Новое" -> Status.NEW;
            case "Выполняется" -> Status.IN_PROGRESS;
            case "Выполнено" -> Status.DONE;
            default -> throw new IllegalStateException("Неожиданное значение статуса " + taskFields[3]);
        };
        Type type = Type.valueOf(taskFields[1]);
        if (type == Type.TASK) {
            task = new Task(Integer.parseInt(taskFields[0]), taskFields[2], taskFields[4], status);
        } else if (type == Type.SUBTASK) {
            task = new Subtask(Integer.parseInt(taskFields[0]), taskFields[2], taskFields[4], status,
                    Integer.parseInt(taskFields[5]));
        } else if (type == Type.EPIC) {
            task = new Epic(Integer.parseInt(taskFields[0]), taskFields[2], taskFields[4]);
        }
        return task;
    }

    //считывание данных из файла
    private void taskAddFromFile(Task task) {
        int currentId = task.getId();
        if (super.id < currentId) {
            id = currentId;
        }
        if (task.getType() == Type.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getType() == Type.SUBTASK) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                subtasks.put(subtask.getId(), subtask);
            } else {
                throw new ManagerLoadException("Ошибка при считывании подзадачи");
            }
        } else {
            tasks.put(task.getId(), task);
        }
    }

    //загрузка данных из файла
    public static FileBackedTaskManager loadFromFile(File tasksFile) {
        if (!tasksFile.exists()) {
            throw new ManagerLoadException("Файл для загрузки отсутствует");
        }
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(tasksFile);
        try (BufferedReader br = Files.newBufferedReader(tasksFile.toPath(), StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                Task task = backedTaskManager.taskFromString(line);
                backedTaskManager.taskAddFromFile(task);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения");
        }
        return backedTaskManager;
    }

    @Override
    public Collection<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public Collection<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public Collection<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int id) {
        super.createSubtask(subtask, id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        return super.getSubtasksByEpicId(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }
}
