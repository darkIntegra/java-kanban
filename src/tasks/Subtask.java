package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    //Основной конструктор
    public Subtask(String name, String description) {
        super(name, description);
        this.epicId = 0; //потенциально, неинициализированное поле может привести к проблемам
    }

    //Конструктор для обновления данных Subtask
    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    //конструктор для тестирования параметров времени
    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = 0;
    }

    //конструктор для taskConverter
    public Subtask(int id, String name, String description, Status status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    //конструктор для тестирования сервера
    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int parentId) {
        super(name, description, startTime, duration);
        this.epicId = parentId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }
}
