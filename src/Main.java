import Manager.TaskManager;
import Tasks.*;

public class Main {
    public static void printTasks(TaskManager manager) {
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
        TaskManager manager = new TaskManager();

        //тестирование
        //добавляю две задачи
        Task task1 = new Task("Tasks.Task #1", "Task1 description");
        Task task2 = new Task("Tasks.Task #2", "Task2 description");
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        //добавляю эпик с двумя подзадачами
        Epic epic1 = new Epic("Tasks.Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Tasks.Subtask #1-1", "Subtask1-1 description", epic1.getId());
        Subtask subtask2 = new Subtask("Tasks.Subtask #1-2", "Subtask1-2 description", epic1.getId());
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        //добавляю эпик с одной подзадачей
        Epic epic2 = new Epic("Tasks.Epic #2", "Epic2 description");
        manager.addNewEpic(epic2);

        Subtask subtask3 = new Subtask("Tasks.Subtask #2-1", "Subtask2-1 description", epic2.getId());
        manager.addNewSubtask(subtask3);

        //Распечатайте списки эпиков, задач и подзадач
        printTasks(manager);

        //Меняю статусы созданных объектов, проверяю
        System.out.println("_________________________________________________________________________________________");
        System.out.println("Обновляем статус Task1 и subTask1 на 'в работе'");
        System.out.println("_________________________________________________________________________________________");

        Task upTask1 = new Task(task1.getId(), "Up Tasks.Task #1", "Up Task1 description", Status.IN_PROGRESS);
        manager.updateTask(upTask1);

        Subtask upSubTask1 = new Subtask(subtask1.getId(), "Up Tasks.Subtask #1-1", "UP Subtask1-1 description",
                Status.IN_PROGRESS, epic1.getId());
        manager.updateSubtask(upSubTask1);

        printTasks(manager);

        System.out.println("_________________________________________________________________________________________");
        System.out.println("Обновляем статус Task2, subTask1, subTask2 на 'выполнено'");
        System.out.println("_________________________________________________________________________________________");


        Task upTask2 = new Task(task2.getId(), "Up Tasks.Task #2", "Up Task2 description", Status.DONE);
        manager.updateTask(upTask2);

        Subtask upSubTask2 = new Subtask(subtask1.getId(), "Up2 Tasks.Subtask #1-1", "UP2 Subtask1-1 description",
                Status.DONE, epic1.getId());
        manager.updateSubtask(upSubTask2);

        Subtask upSubTask3 = new Subtask(subtask2.getId(), "Up Tasks.Subtask #2-1", "UP Subtask2-1 description",
                Status.DONE, epic1.getId());
        manager.updateSubtask(upSubTask3);

        printTasks(manager);

        System.out.println("_________________________________________________________________________________________");
        System.out.println("статус Epic1 сменился на 'выполнено'. Добавим еще один сабтаск в Epic1");
        System.out.println("_________________________________________________________________________________________");

        Subtask subtask4 = new Subtask("Tasks.Subtask #1-3", "Subtask1-3 description", epic1.getId());
        manager.addNewSubtask(subtask4);

        printTasks(manager);

        //удаляю одну из задач и один из эпиков
        manager.deleteTask(task2.getId());
        manager.deleteEpic(epic2.getId());
        manager.deleteSubTask(subtask4.getId());

        System.out.println("_________________________________________________________________________________________");
        System.out.println("Удаляю Task2, subTask4 и Epic2 - статус Epic1 должен смениться на DONE");
        System.out.println("_________________________________________________________________________________________");

        printTasks(manager);
    }
}
