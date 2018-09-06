package com.ztkmkoo.todolist.frontend.model;

import com.ztkmkoo.todolist.common.model.ToDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToDoAns {
    private ToDo toDo;
    private int toDoReqResult;

    public ToDoAns(final int toDoReqResult) {
        this.toDoReqResult = toDoReqResult;
    }
}
