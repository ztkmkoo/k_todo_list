package com.ztkmkoo.todolist.common.message;

import com.ztkmkoo.todolist.common.model.ToDo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
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
        private ToDo toDo;
        private int totalCount;
    }

    @Getter
    @AllArgsConstructor
    public static class SelectToDoReq implements Serializable {
        private int page;
        private int countPerPage;
    }

    @Getter
    @AllArgsConstructor
    public static class SelectToDoAns implements Serializable {
        private int page;
        private int countPerPage;
        private List<ToDo> toDoList;
        private int totalPage;
    }

    @Getter
    @AllArgsConstructor
    public static class FinishToDoReq implements Serializable {
        private int id;
    }

    @Getter
    @AllArgsConstructor
    public static class FinishToDoAns implements Serializable {
        private int id;
        private int isDone;
        private String errorLog;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateToDoReq implements Serializable {
        private int id;
        private String toDo;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateToDoAns implements Serializable {
        private int id;
        private boolean isDone;
        private String errorLog;
    }
}
