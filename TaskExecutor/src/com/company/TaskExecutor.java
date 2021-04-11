package com.company;

import java.util.*;

public class TaskExecutor {
    private final Set<Task> completed = new HashSet<>();

    void execute(Collection<Task> tasks) {
        List<Set<Task>> byPriority = orderByPriority(tasks);
        for (int i = byPriority.size() - 1; i >= 0; --i) {
            for (Task task: byPriority.get(i)) {
                execute(task);
            }
        }
    }

    private void execute(Task task) {
        for (Task subTask: task.dependencies()) {
            if (!completed.contains(subTask)) {
                execute(subTask);
            }
        }
        task.execute();
        completed.add(task);
    }

    private List<Set<Task>> orderByPriority(Collection<Task> tasks) {
        Map<Task, Integer> tasksWithPriorities = new HashMap<>();
        // рекурсивно подсчитываем приоритеты (сколько раз понадобилась каждая задача)
        for (Task task: tasks) {
            calculatePriorities(tasksWithPriorities, task);
        }

        int maxPriority = tasksWithPriorities.values().stream().max(Integer::compareTo).orElse(0) + 1;
        List<Set<Task>> byPriority = new ArrayList<>(maxPriority);
        for (int i = 0; i < maxPriority; ++i) {
            byPriority.add(new HashSet<>());
        }

        // упорядочиваем по приоритету
        for (Map.Entry<Task, Integer> entry: tasksWithPriorities.entrySet()) {
            byPriority.get(entry.getValue()).add(entry.getKey());
        }
        return byPriority;
    }

    private void calculatePriorities(Map<Task, Integer> tasksWithPriorities, Task task){

        Integer oldPriority = tasksWithPriorities.getOrDefault(task, 0);
        tasksWithPriorities.put(task, oldPriority + 1);

        for (Task subTask: task.dependencies()) {
            calculatePriorities(tasksWithPriorities, subTask);
        }
    }
}
