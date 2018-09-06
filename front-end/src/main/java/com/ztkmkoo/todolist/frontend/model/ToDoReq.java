package com.ztkmkoo.todolist.frontend.model;

import lombok.Data;

import java.util.List;

@Data
public class ToDoReq {
    private String toDo;
    private List<Integer> refList;
}
