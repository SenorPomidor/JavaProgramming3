package ru.mipt.tasks.entity;

public class Task {

    private final Long taskId;
    private final String content;

    public Task(Long taskId, String content) {
        this.taskId = taskId;
        this.content = content;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getContent() {
        return content;
    }

}
