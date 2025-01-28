package tasks;

import manager.InMemoryTaskManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    private InMemoryTaskManager taskManager;

    @Before
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testFullConstructorWithAllFields() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask = new Subtask(1, "Task Name", "Description", Status.DONE, 2, startTime, duration);
        assertEquals(1, subtask.getId());
        assertEquals("Task Name", subtask.getName());
        assertEquals("Description", subtask.getDescription());
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(2, subtask.getEpicId());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(duration, subtask.getDuration());
    }

    // Проверка наличия связанного эпика для подзадачи
    @Test
    public void testSubtaskHasLinkedEpic() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask, epic.getId());

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertNotNull(updatedEpic);
        assertEquals(1, updatedEpic.getSubtaskIds().size());
        assertEquals(Integer.valueOf(subtask.getId()), updatedEpic.getSubtaskIds().get(0));
    }


}