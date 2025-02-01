package manager;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import utility.TaskConverter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    //запись данных в файл
    public void save() {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
            try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
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


    //загрузка данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                Task task = TaskConverter.taskFromString(line);
                if (task.getType() == Type.TASK) {
                    backedTaskManager.createTask(task);
                } else if (task.getType() == Type.SUBTASK) {
                    Subtask subtask = (Subtask) task;
                    backedTaskManager.createSubtask(subtask, subtask.getEpicId());
                } else if (task.getType() == Type.EPIC) {
                    backedTaskManager.createEpic((Epic) task);
                }
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
