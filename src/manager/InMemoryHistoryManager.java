package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        int historyListSize;
        if (task != null) {
            historyListSize = historyList.size();
            if (historyListSize == 10) {
                historyList.removeFirst();
            }
            historyList.add(task);
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
}
