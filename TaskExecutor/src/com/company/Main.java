package com.company;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Task task1 = new TaskImpl(null);
        Task task2 = new TaskImpl(null);
        Task task3 = new TaskImpl(Arrays.asList(task1, task2));
        Task task4 = new TaskImpl(Arrays.asList(task3, task2));
        Task task5 = new TaskImpl(Arrays.asList(task3, task2));
        Task task6 = new TaskImpl(Arrays.asList(task1, task2));

        TaskExecutor executor = new TaskExecutor();
        executor.execute(Arrays.asList(task1, task2, task3, task4, task5, task6));
    }
}
