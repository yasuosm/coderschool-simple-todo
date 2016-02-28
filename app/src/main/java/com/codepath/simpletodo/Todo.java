package com.codepath.simpletodo;

/**
 * Created by annt on 2/27/16.
 */
public class Todo {
    private int id;
    private String text;
    private long dueTimestamp;

    public Todo(int id, String text, long dueTimestamp) {
        this.id = id;
        this.text = text;
        this.dueTimestamp = dueTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDueTimestamp() {
        return dueTimestamp;
    }

    public void setDueTimestamp(long dueTimestamp) {
        this.dueTimestamp = dueTimestamp;
    }
}
