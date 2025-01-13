package manager;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager manger = getDefaultHistory();
        return new InMemoryTaskManager(manger);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}