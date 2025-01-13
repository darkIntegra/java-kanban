package manager;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import utility.TaskConverter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksFile;

    //для работы с историей задач
    public FileBackedTaskManager(File tasksFile) {
        super(Managers.getDefaultHistory());
        this.tasksFile = tasksFile;
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
                    fileWriter.write(TaskConverter.taskToString(task) + "\n");
                }
                for (Epic epic : getEpics()) {
                    fileWriter.write(TaskConverter.taskToString(epic) + "\n");
                }
                for (Subtask subtask : getSubtasks()) {
                    fileWriter.write(TaskConverter.taskToString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    //считывание данных из файла
    private void taskAddFromFile(Task task) {
        int currentId = task.getId();
        if (id < currentId) {
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
                Task task = TaskConverter.taskFromString(line);
                backedTaskManager.taskAddFromFile(task);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения");
        }
        return backedTaskManager;
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
}
