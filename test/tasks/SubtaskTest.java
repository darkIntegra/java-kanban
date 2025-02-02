package tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SubtaskTest {

    @Before
    public void setUp() {
        // Создаем эпик для тестирования связи с подзадачей
        Epic epic = new Epic("Эпик", "Описание эпика");
    }

    @Test
    public void testFullConstructorWithAllFields() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(60);
        Subtask subtask = new Subtask(1, "Имя подзадачи", "Описание подзадачи",
                Status.DONE, 2, startTime, duration);

        // Проверяем все поля подзадачи
        assertEquals(1, subtask.getId());
        assertEquals("Имя подзадачи", subtask.getName());
        assertEquals("Описание подзадачи", subtask.getDescription());
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(2, subtask.getEpicId());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(duration, subtask.getDuration());
    }

    @Test
    public void testSubtaskHasLinkedEpic() {
        Subtask subtask = new Subtask("Подзадача 1", "Содержание подзадачи", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));

        // Устанавливаем связь с эпиком
        int epicId = 1; // Пример ID эпика
        subtask.setEpicId(epicId);

        // Проверяем, что подзадача корректно связана с эпиком
        Assertions.assertEquals(epicId, subtask.getEpicId(), "ID эпика не совпадает");
    }

    @Test
    public void testEndTimeCalculation() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);
        Subtask subtask = new Subtask("Подзадача 1", "Содержание подзадачи",
                Status.NEW, startTime, duration);

        // Проверяем расчет времени окончания
        LocalDateTime expectedEndTime = startTime.plus(duration);
        Assertions.assertEquals(expectedEndTime, subtask.getEndTime(),
                "Время окончания подзадачи рассчитано некорректно");
    }
}