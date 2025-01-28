
import manager.InMemoryTaskManager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void printTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            for (Subtask subtask : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("- Подзадача: " + subtask);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello Java");
    }
}
