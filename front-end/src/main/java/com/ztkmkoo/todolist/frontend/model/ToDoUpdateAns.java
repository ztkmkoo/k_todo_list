package com.ztkmkoo.todolist.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToDoUpdateAns {
    private int isDone;
    private String errorLog;
}
