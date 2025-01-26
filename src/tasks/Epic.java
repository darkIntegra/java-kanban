package tasks;

import manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    //Основной конструктор
    public Epic(String name, String description) {
        super(name, description);
    }

    //Конструктор для обновления эпика
    public Epic(int id, String name, String description) {
        super(id, name, description);
        //Раз эпик обновляется, значит произошел старт задачи, присваиваем duration zero
        Duration duration = Duration.ZERO;
    }

    //пока не понимаю какие именно нужны конструкторы, но пусть будет для 8 спринта
    public Epic(String name, String description, LocalDateTime localDateTime, Duration duration) {
        super(name, description, localDateTime, duration);
    }

    //Гетеры и сеттеры
    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    //Методы работы с перечнем сабтасков
    public void deleteSubtaskIds(int id) {
        subtaskIds.remove(Integer.valueOf(id));
        calculateFields();
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
        calculateFields();
    }

    private void calculateFields() {
        if (!subtaskIds.isEmpty()) {
            Duration totalDuration = Duration.ZERO;
            LocalDateTime earliestStart = null;
            LocalDateTime latestEnd = null;

            for (Integer subtaskId : subtaskIds) {
                Subtask subtask = (Subtask) InMemoryTaskManager.getInstance().getSubtaskById(subtaskId);

            }
        }
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
