package com.company;

import java.util.Collection;
import java.util.HashSet;

public class TaskImpl implements Task {
    @Override
    public void execute() {
        System.out.println("executed: " + this);
        for (Task task: dependencies()) {
            System.out.println("  needed: " + task.toString());
        }
    }

    private final Collection<Task> dependencies;

    public TaskImpl(Collection<Task> dependencies) {
        if (dependencies == null) {
            this.dependencies = new HashSet<>();
        } else {
            this.dependencies = dependencies;
        }
    }

    @Override
    public Collection<Task> dependencies() {
        return dependencies;
    }
}
