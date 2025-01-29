package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    //основной конструктор
    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    //конструктор для создания Task
    public Task(String name, String description) {
        this(0, name, description, Status.NEW, LocalDateTime.now(), Duration.ZERO);
    }

    //конструктор для обновления данных Task
    public Task(int id, String name, String description, Status status) {
        this(id, name, description, status, LocalDateTime.now(), Duration.ZERO);
    }

    //конструктор для обновления данных Epic
    public Task(int id, String name, String description) {
        this(id, name, description, Status.NEW, LocalDateTime.now(), Duration.ZERO);
    }

    //конструктор для тестирования параметров времени
    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this(0, name, description, status, startTime, duration);
    }

    //Гетеры и сетеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return (startTime == null || duration == null) ? null : startTime.plus(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name= '" + getName() + '\'' +
                ", description= '" + getDescription() + '\'' +
                ", id= " + getId() +
                ", status= " + getStatus() +
                ", startTime= " + (getStartTime() == null ? "не установлено" : getStartTime()) +
                ", duration= " + (getDuration() == null ? "не установлена" : getDuration().toHours() + " часов " +
                getDuration().toMinutesPart() + " минут") +
                ", endTime= " + (getEndTime() == null ? "не установлено" : getEndTime()) +
                '}';
    }

    public Type getType() {
        return Type.TASK;
    }

}
