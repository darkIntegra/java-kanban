package utility;

import tasks.*;

public class TaskConverter {
    public static String taskToString(Task task) {
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

    public static Task taskFromString(String str) {
        String[] taskFields = str.split(",");
        int id = Integer.parseInt(taskFields[0]);
        Type type = Type.valueOf(taskFields[1]);
        String name = taskFields[2];
        Status status = switch (taskFields[3]) {
            case "Новое" -> Status.NEW;
            case "Выполняется" -> Status.IN_PROGRESS;
            case "Выполнено" -> Status.DONE;
            default -> throw new IllegalStateException("Неожиданное значение статуса " + taskFields[3]);
        };
        String description = taskFields[4];
        Task task;
        switch (type) {
            case TASK:
                task = new Task(id, name, description, status);
                break;
            case EPIC:
                task = new Epic(id, name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(taskFields[5]);
                task = new Subtask(id, name, description, status, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        return task;
    }
}
