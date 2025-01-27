package tasks;

import manager.InMemoryTaskManager;
import manager.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private Duration calculatedDuration = Duration.ZERO;
    private LocalDateTime calculatedStartTime = null;
    private LocalDateTime calculatedEndTime = null;
    private final InMemoryTaskManager taskManager; // Добавляем поле для хранения ссылки на менеджер задач

    //Основной конструктор
    public Epic(String name, String description) {
        super(name, description);
        this.taskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    //Конструктор для обновления эпика
    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.taskManager = (InMemoryTaskManager) Managers.getDefault();
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
        calculateFields();
    }

    public void deleteSubtaskIds(int id) {
        subtaskIds.remove(Integer.valueOf(id));
        calculateFields();
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void calculateFields() {
        if (!subtaskIds.isEmpty()) {
            Duration totalDuration = Duration.ZERO;
            LocalDateTime earliestStart = null;
            LocalDateTime latestEnd = null;

            for (Integer subtaskId : subtaskIds) {
                Subtask subtask = taskManager.getSubtaskById(subtaskId);
                if (subtask != null) {
                    totalDuration = totalDuration.plus(subtask.getDuration());
                    if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                        earliestStart = subtask.getStartTime();
                    }
                    LocalDateTime subtaskEnd = subtask.getEndTime();
                    if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                        latestEnd = subtaskEnd;
                    }
                }
            }
            this.calculatedDuration = totalDuration;
            this.calculatedStartTime = earliestStart;
            this.calculatedEndTime = latestEnd;
        } else {
            this.calculatedDuration = Duration.ZERO;
            this.calculatedStartTime = null;
            this.calculatedEndTime = null;
        }
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
