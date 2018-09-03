package com.ztkmkoo.todolist.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDo {
    private int id;
    private String toDo;
    private List<Integer> refList;
    private LocalDateTime registerDate;
    private LocalDateTime modifyDate;
    private int isDone;
}
