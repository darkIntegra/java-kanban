package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MangersTest {
    @Test
    void taskManagerTest() {
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager);
    }

    @Test
    void historyManagerTest() {
        HistoryManager manager = Managers.getDefaultHistory();
        Assertions.assertNotNull(manager);
    }
}
