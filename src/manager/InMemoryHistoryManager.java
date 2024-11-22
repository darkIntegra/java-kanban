package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() == 10) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }

    @Override
    public void getHistory() {
        System.out.println("История просмотров:");
        for (Task task : historyList) {
            System.out.println(task);
        }
    }
}
