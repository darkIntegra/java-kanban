package tasks;

import manager.InMemoryTaskManager;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class EpicTest {
    private InMemoryTaskManager taskManager;

    @Before
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAllSubtasksNew() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testAllSubtasksDone() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.DONE,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpic(epic);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testMixedSubtasksNewAndDone() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE, LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testSubtasksInProgress() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testEmptySubtasks() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);
        taskManager.updateEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    // Проверка корректности расчёта статуса эпика на основании состояния подзадач
    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", Status.DONE,
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(90));

        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.createSubtask(subtask3, epic.getId());

        taskManager.updateEpic(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}