package com.ztkmkoo.todolist.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Message {

    @Getter
    @AllArgsConstructor
    public static class InsertToDoReq implements Serializable {
        private String toDo;
        private List<Integer> refList;
    }

    @Getter
    @AllArgsConstructor
    public static class InsertToDoAns implements Serializable {
        private int id;
        private String toDo;
        private List<Integer> refList;
        private LocalDateTime registerDate;
        private LocalDateTime modifyDate;
        private int isDone;
    }
}
