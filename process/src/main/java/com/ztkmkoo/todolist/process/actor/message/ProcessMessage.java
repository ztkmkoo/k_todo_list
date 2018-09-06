package com.ztkmkoo.todolist.process.actor.message;

import com.ztkmkoo.todolist.common.message.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ProcessMessage {
    public static class StartMasterActor implements Serializable {
        private final boolean initSample;

        public StartMasterActor(final boolean initSample) {
            this.initSample = initSample;
        }

        public boolean isInitSample() {
            return initSample;
        }
    }

    public static class StartInsertIgniteSample implements Serializable {
        private final List<Message.InsertToDoReq> list;

        public StartInsertIgniteSample(List<Message.InsertToDoReq> list) {
            this.list = Collections.unmodifiableList(list);
        }

        public List<Message.InsertToDoReq> getList() {
            return list;
        }
    }
}
