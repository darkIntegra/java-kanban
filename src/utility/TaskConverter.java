package utility;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TaskConverter {
    public static String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(
                ",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                String.valueOf(task.getDuration().toMinutes()),
                task.getStartTime() != null ? task.getStartTime().toString() : "null"
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
        long durationInMinutes = Long.parseLong(taskFields[5]);
        LocalDateTime startTime = "null".equals(taskFields[6]) ? null : LocalDateTime.parse(taskFields[6]);
        Task task;
        switch (type) {
            case TASK:
                task = new Task(id, name, description, status, startTime, Duration.ofMinutes(durationInMinutes));
                break;
            case EPIC:
                task = new Epic(id, name, description);
                ((Epic) task).setSubtaskIds(new ArrayList<>());
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(taskFields[5]);
                task = new Subtask(id, name, description, status, epicId, startTime,
                        Duration.ofMinutes(durationInMinutes));
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        return task;
    }
}
