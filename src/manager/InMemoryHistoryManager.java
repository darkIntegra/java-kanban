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
            System.out.println("Таск не может быть null");
            return;
        }

        // Проверяю наличие задачи в истории
        Node existingNode = historyHashMap.get(task.getId());
        if (existingNode != null) {
            System.out.println("Таск с ID " + task.getId() + " уже существует. Удаляем старую запись.");
            removeNode(existingNode);
        }

        // Создаю новую ноду
        Node newNode = new Node();
        newNode.values = task;

        // Добавляю новую ноду в список
        if (first == null) { // Если список пустой
            first = newNode;
            last = newNode;
        } else { // Если список не пустой
            newNode.previous = last;
            last.next = newNode;
            last = newNode;
        }
        historyHashMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node.previous != null) {  // Удаление ссылки на текущую ноду из связанного списка
            node.previous.next = node.next;
        } else { // Если у узла нет предыдущей ноды значит он первый
            first = node.next;
        }
        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            last = node.previous;
        }
        historyHashMap.remove(node.values.getId());
    }

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
