package tasks;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EpicTest {
    private Epic epic;

    @Before
    public void setUp() {
        epic = new Epic("Эпик", "Описание эпика");
    }

    @Test
    public void testAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Содержание 2", Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        epic.updateStatus(subtasks); // Передаем список подзадач
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.DONE,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Содержание 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        epic.updateStatus(subtasks);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testMixedSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Содержание 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        epic.updateStatus(subtasks);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testEmptySubtasks() {
        epic.updateStatus(new ArrayList<>()); // Пустой список подзадач
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testSubtasksInProgress() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Содержание 1", Status.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(30));

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);

        epic.updateStatus(subtasks);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}