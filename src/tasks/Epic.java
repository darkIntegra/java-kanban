package tasks;

import java.time.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    public Duration calculatedDuration = Duration.ZERO;
    public LocalDateTime calculatedStartTime = null;
    public LocalDateTime calculatedEndTime = null;

    //Основной конструктор
    public Epic(String name, String description) {
        super(name, description);
    }

    //Конструктор для обновления эпика
    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    //Гетеры и сеттеры
    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public LocalDateTime getCalculatedStartTime() {
        return calculatedStartTime;
    }

    public LocalDateTime getCalculatedEndTime() {
        return calculatedEndTime;
    }

    //Методы работы с перечнем сабтасков
    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void deleteSubtaskIds(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public Duration getDuration() {
        return calculatedDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }
}
