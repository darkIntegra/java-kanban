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

        //создаю объекты по ТЗ6
        Task task1 = new Task("таска 1", "содержание 1");
        Task task2 = new Task("таска 2", "содержание 1");
        Epic epic1 = new Epic("эпик1", "содержание 1");
        Epic epic2 = new Epic("эпик2", "содержание 2");
        Subtask subtask1 = new Subtask("сабтаск1.1", "содержание 1");
        Subtask subtask2 = new Subtask("сабтаск1.2", "содержание 1");
        Subtask subtask3 = new Subtask("сабтаск1.3", "содержание 3");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic1.getId());

        //запрашиваю созданные задачи в разном порядке, после каждого создания запрашиваю историю
        history.add(task1);
        history.add(task1);
        history.add(epic2);
        history.add(subtask3);
        history.add(subtask1);
        history.add(epic1);
        history.add(epic1);
        history.add(task2);
        history.add(subtask2);
        System.out.println(history.getHistory());

        //удаляю задачу из истории
        history.remove(task2.getId());
        System.out.println(history.getHistory());

        //удаляю эпик с тремя подзадачами
        history.remove(epic1.getId());
        System.out.println(history.getHistory());
    }
}
