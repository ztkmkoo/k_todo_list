package com.ztkmkoo.todolist.process;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ztkmkoo.todolist.common.message.Message;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public class ProcessApplication {
    public static void main(String[] args) throws InterruptedException {
        final ActorSystem actorSystem = ActorSystem.create("ActorSystem");

        ActorRef actorRef = actorSystem.actorOf(Props.create(TestActor.class), "test");
        System.out.println(actorRef.path());

        while (true) {
            Thread.sleep(1000);
        }
    }

    public static void start() {
        try (Ignite ignite = Ignition.start()){

            IgniteCache cache = ignite.getOrCreateCache(
                    new CacheConfiguration<>().setName("hi2").setSqlSchema("PUBLIC")
            );
//                cache.query(new SqlFieldsQuery("CREATE TABLE hi2 (id int PRIMARY KEY, name VARCHAR)"));

            SqlFieldsQuery iQuery = new SqlFieldsQuery("INSERT INTO hi2 (id, name) VALUES (?, ?)");
            cache.query(iQuery.setArgs(1, "Kebron")).getAll();
            cache.query(iQuery.setArgs(2, "Kim5")).getAll();
            cache.query(iQuery.setArgs(3, "Kebron")).getAll();
            cache.query(iQuery.setArgs(4, "Kebron")).getAll();
            cache.query(iQuery.setArgs(5, "Kebron")).getAll();
            cache.query(iQuery.setArgs(6, "Kebron")).getAll();

            SqlFieldsQuery sQuery = new SqlFieldsQuery("SELECT h.id, h.name " +
                    " FROM hi2 h WHERE h.name = ? ORDER BY h.id LIMIT 3");

            final int pageSize = 3;
//                List list = cache.query(sQuery.setArgs("Kebron").setPageSize(pageSize)).getAll();
//                list.isEmpty();

            FieldsQueryCursor<List> cursor = cache.query(sQuery.setArgs("Kebron").setPageSize(pageSize));


            int count = 1;
            Iterator<List> iterator = cursor.iterator();
            while (iterator.hasNext()) {
                List list = iterator.next();
                System.out.println(count++ + ":" + list.toString());
            }
        }
    }

    static class TestActor extends AbstractActor {
        LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Message.InsertToDoReq.class, msg -> {
                        log.info("Receive Message.InsertToDoReq {}", msg);

                        LocalDateTime now = LocalDateTime.now();
                        Message.InsertToDoAns ans = new Message.InsertToDoAns(3, msg.getToDo(), msg.getRefList(), now, now, 0);
                        sender().tell(ans, self());
                    })
                    .matchAny(o -> {
                        System.out.println(o.toString());
                        getContext().getSystem().scheduler().scheduleOnce(Duration.ofSeconds(3), sender(), o, getContext().dispatcher(), self());
                    })
                    .build();
        }
    }
}
