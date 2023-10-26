package ru.mipt.tasks.entity;

import java.util.List;

public class Person {

    private final Long id;
    private final String name;
    private final String lastName;
    private final List<Task> tasks;

    public Person(Long id, String name, String lastName, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}

