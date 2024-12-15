package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyHashMap = new HashMap<>();
    private Node first;
    private Node last;

    static class Node {
        Node previous;
        Node next;
        Task values;
    }

    @Override
    public void add(Task task) {
        if (last == null && first == null) {
            Node node = new Node();
            node.values = task;
            first = node;
            last = node;
            historyHashMap.put(task.getId(), node);
        } else if (!historyHashMap.containsKey(task.getId())) {
            Node node = new Node();
            node.values = task;
            node.previous = last;
            historyHashMap.get(last.values.getId()).next = node;
            last = node;
            historyHashMap.put(task.getId(), node);
        } else if (historyHashMap.get(task.getId()) != last) {
            if (historyHashMap.get(task.getId()).previous != null) {
                historyHashMap.get(task.getId()).previous.next = historyHashMap.get(task.getId()).next;
            } else {
                first = historyHashMap.get(task.getId()).next;
            }
            historyHashMap.get(task.getId()).next.previous = historyHashMap.get(task.getId()).previous;
            historyHashMap.get(last.values.getId()).next = historyHashMap.get(task.getId());
            historyHashMap.get(task.getId()).previous = last;
            last = historyHashMap.get(task.getId());
            historyHashMap.get(task.getId()).next = null;
        }
    }

    @Override
    public void remove(int id) {
        if (historyHashMap.containsKey(id)) {
            if (!historyHashMap.get(id).equals(first)) {
                historyHashMap.get(id).previous.next = historyHashMap.get(id).next;
            } else {
                first = historyHashMap.get(id).next;
            }
            if (!historyHashMap.get(id).equals(last)) {
                historyHashMap.get(id).next.previous = historyHashMap.get(id).previous;
            } else {
                last = historyHashMap.get(id).previous;
            }
            historyHashMap.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node currentNode = first;
        for (int i = 0; i < historyHashMap.size(); i++) {
            historyList.add(currentNode.values);
            currentNode = currentNode.next;
        }
        return historyList;
    }
}
