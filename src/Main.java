import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

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
        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();

        Task task1 = new Task("таска 1", "содержание 1");
        Task task2 = new Task("таска 2", "содержание 1");

        Epic epic1 = new Epic("эпик1", "содержание 1");

        Subtask subtask1 = new Subtask("сабтаск1.1", "содержание 1");
        Subtask subtask2 = new Subtask("сабтаск1.2", "содержание 1");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());

        Task task11 = new Task(task1.getId(), "Таска11", "содержании11", Status.IN_PROGRESS);
        manager.updateTask(task11);
        Subtask subtask11 = new Subtask(subtask1.getId(), "сабтаск1.11", "содержание 11", Status.IN_PROGRESS, epic1.getId());
        manager.updateSubtask(subtask11);

        Epic epic11 = new Epic(epic1.getId(), "эпик22", "содержание 22");
        manager.updateEpic(epic11);

        /*printTasks(manager);*/

        Subtask subtask22 = new Subtask(subtask1.getId(), "сабтаск2.11", "содержание 12", Status.DONE, epic1.getId());
        Subtask subtask23 = new Subtask(subtask2.getId(), "сабтаск2.12", "содержание 12", Status.DONE, epic1.getId());
        manager.updateSubtask(subtask22);
        manager.updateSubtask(subtask23);

        /*printTasks(manager);*/

        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask22.getId());
        manager.getEpicById(epic1.getId());

        manager.getHistory();

        history.add(task1);
        history.add(task2);
        history.add(epic1);
        history.add(subtask23);

        history.getHistory();
    }
}
