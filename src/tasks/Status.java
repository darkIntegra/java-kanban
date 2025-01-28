package tasks;

public enum Status {
    NEW("Новое"),
    IN_PROGRESS("Выполняется"),
    DONE("Выполнено");

    final String name;

    Status(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
