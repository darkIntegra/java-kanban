package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyHashMap = new HashMap<>();
    private Node first;
    private Node last;

    private static class Node {
        Node previous;
        Node next;
        Task values;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            // System.out.println("Таск не может быть null"); удалил, чтобы логи в тестах были чисты
            return;
        }

        // Проверяю наличие задачи в истории
        Node existingNode = historyHashMap.get(task.getId());
        if (existingNode != null) {
            remove(task.getId());
        }

        // Создаю новую ноду
        Node newNode = new Node();
        newNode.values = task;

        // Добавляю новую ноду в список
        if (first == null) { // Если список пустой
            first = newNode;
        } else { // Если список не пустой
            newNode.previous = last;
            last.next = newNode;
        }
        last = newNode;
        historyHashMap.put(task.getId(), newNode);
    }
//метод removeNode(удалено) выделил отдельно для удобства читабельности. Оказалось что remove его прекрасно заменяет

    @Override
    public void remove(int id) {
        Node nodeToRemove = historyHashMap.get(id); //один раз достаю из мапы
        if (nodeToRemove == null) {
            return;
        }
        if (nodeToRemove == first && nodeToRemove == last) { //прорабатываю крайние случаи отдельно
            first = null;
            last = null;
        } else if (nodeToRemove == first) {
            first = nodeToRemove.next;
            if (first != null) {
                first.previous = null;
            }
        } else if (nodeToRemove == last) {
            last = nodeToRemove.previous;
            if (last != null) {
                last.next = null;
            }
        } else {
            nodeToRemove.previous.next = nodeToRemove.next;
            nodeToRemove.next.previous = nodeToRemove.previous;
        }
        historyHashMap.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null) {
            historyList.add(currentNode.values);
            currentNode = currentNode.next;
        }
        return historyList;
    }
}
