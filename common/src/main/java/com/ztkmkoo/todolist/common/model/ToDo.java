package com.ztkmkoo.todolist.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDo implements Serializable{
    private int id;
    private String toDo;
    private List<Integer> refList;
    private Timestamp registerDate;
    private Timestamp modifyDate;
    private int isDone;
}
