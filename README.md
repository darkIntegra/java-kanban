Выполнены требования из технического задания sprint 7:

  - добавлена реализация менеджера, FileBackedTaskManager. В нем разработана функциональность для сохранения и загрузки задач в файл,
    что позволяет управлять состоянием задач между сессиями работы программы. Это обеспечивает персистентность данных и возможность
    восстановления состояния менеджера задач после перезапуска приложения.
  - подход к тестированию был изменен. Тесты FileBackedTaskManager, InMemoryTaskManager и InMemoryHistoryManager выполнены в едином стиле.

Выполнены требования из технического задания sprint 6:

  - удалено ограничение на историю просмотров;
  - реализован механизм убирающий повторы в истории просмотров;
  - оптимизированно время выполнения задач с историей просмотров;
  - добавлено покрытие Unit тестами;
  - выполнено дополнительное задание с пользовательским сценарием в main.
