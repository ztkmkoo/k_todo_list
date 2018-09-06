package com.ztkmkoo.todolist.process.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ztkmkoo.todolist.common.message.Message;
import com.ztkmkoo.todolist.common.model.ToDo;
import com.ztkmkoo.todolist.process.actor.message.ProcessMessage;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class MasterActor extends AbstractActor {

    public static Props props(final Ignite ignite) {
        return Props.create(MasterActor.class, () -> new MasterActor(ignite));
    }

    public static Message.InsertToDoReq sampleInsertToDoReqCreator(final String toDo) {
        return sampleInsertToDoReqCreator(toDo, Collections.EMPTY_LIST);
    }

    public static Message.InsertToDoReq sampleInsertToDoReqCreator(final String toDo, final int ref) {
        return new Message.InsertToDoReq(toDo, Arrays.asList(ref));
    }

    public static Message.InsertToDoReq sampleInsertToDoReqCreator(final String toDo, final List refList) {
        return new Message.InsertToDoReq(toDo, refList);
    }

    private static String listToJsonString(final List<Integer> list) throws JsonProcessingException {
        if (list.isEmpty())
            return "";

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }

    private static List<Integer> jsonStringToList(final String json) throws IOException {
        if (json == null || json.isEmpty())
            return Collections.EMPTY_LIST;

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, List.class);
    }

    private static final String toDoTableName = "todo";
    private static final String refTableName = "ref";
    private static final String schema = "PUBLIC";

    // create
    private final SqlFieldsQuery createToDoTableQuery = new SqlFieldsQuery("CREATE TABLE " + toDoTableName +
            " (id INT PRIMARY KEY, toDo VARCHAR, refList VARCHAR, registerDate TIMESTAMP, modifyDate TIMESTAMP, isDone BOOLEAN)");
    private final SqlFieldsQuery createRefTableQuery = new SqlFieldsQuery("CREATE TABLE " + refTableName +
            " (id INT, refId INT, isDone BOOLEAN, PRIMARY KEY(id, refId))");

    // insert
    private final SqlFieldsQuery insertToDoQuery = new SqlFieldsQuery("INSERT INTO " + toDoTableName +
            " (id, toDo, refList, registerDate, modifyDate, isDone) VALUES (?, ?, ?, ?, ?, ?)");
    private final SqlFieldsQuery insertRefQuery = new SqlFieldsQuery("INSERT INTO " + refTableName +
            " (id, refId, isDone) VALUES (?, ?, ?)");

    // select
    private final SqlFieldsQuery selectToDoCountQuery =  new SqlFieldsQuery("select count(*) from " + toDoTableName);
    private final SqlFieldsQuery selectToDoQuery =  new SqlFieldsQuery("SELECT * FROM " + toDoTableName + " ORDER BY id DESC LIMIT ? OFFSET ?");
    private final SqlFieldsQuery selectRefByIdQuery =  new SqlFieldsQuery("SELECT * FROM " + refTableName + " WHERE id = ?");

    //update
    private final SqlFieldsQuery updateToDoIsDoneQuery = new SqlFieldsQuery("UPDATE " + toDoTableName + " SET isDone = ?, modifyDate = ? WHERE id = ?");
    private final SqlFieldsQuery updateToDoToDoQuery = new SqlFieldsQuery("UPDATE " + toDoTableName + " SET toDo = ?, modifyDate = ? WHERE id = ?");
    private final SqlFieldsQuery updateRefIsDoneQuery = new SqlFieldsQuery("UPDATE " + refTableName + " SET isDone = ? WHERE id = ?");

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Ignite ignite;
    private final List<Message.InsertToDoReq> sampleToReqList = new ArrayList<>(Arrays.asList(
            sampleInsertToDoReqCreator("집안일"),  // 1
            sampleInsertToDoReqCreator("빨래", 1),    // 2
            sampleInsertToDoReqCreator("청소", 1),    // 3
            sampleInsertToDoReqCreator("방청소", Arrays.asList(1, 3)), // 4
            sampleInsertToDoReqCreator("빨래감 종류별로 구별하기", Arrays.asList(1, 2)),   // 5
            sampleInsertToDoReqCreator("세탁물 모두 건조실에 널기", Arrays.asList(1, 2)),  // 6
            sampleInsertToDoReqCreator("음식하기 ", 1), // 7
            sampleInsertToDoReqCreator("재료 씻고 다듬기", Arrays.asList(1, 7)) // 8
    ));

    private int idGenerator = 0;
    private int totalCount = 0;

    private MasterActor(final Ignite ignite) {
        log.info("Start Master Actor: {}", self().path());
        this.ignite = ignite;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessMessage.StartMasterActor.class, msg -> initIgniteInMemoryDatabase(msg.isInitSample()))
                .match(ProcessMessage.StartInsertIgniteSample.class, msg -> {
                    log.info("Receive ProcessMessage.StartInsertIgniteSample: {}", msg);
                    msg.getList().forEach(insertToDoReq -> self().tell(insertToDoReq, ActorRef.noSender()));
                })
                .match(Message.InsertToDoReq.class, msg -> {
                    log.info("Receive Message.InsertToDoReq {}", msg.getToDo());

                    Message.InsertToDoAns ans = insertToDo(msg);
                    if (ans != null) {
                        sender().tell(ans, self());
                        log.info("Insert ToDo Done. {}:{}:{}[Total: {}]",
                                ans.getToDo().getId(), ans.getToDo().getToDo(), ans.getToDo().getRefList(), totalCount);
                    } else {
                        // insert failed => execute after 500 ms later.
                        getContext().getSystem().scheduler().scheduleOnce(
                                Duration.ofMillis(500),
                                self(),
                                msg,
                                getContext().dispatcher(),
                                self()
                        );
                    }
                })
                .match(Message.SelectToDoReq.class, msg -> {
                    log.info("Receive Message. SelectToDoReq {} page ({} c/p).", msg.getPage(), msg.getCountPerPage());

                    List<ToDo> list = selectToDo(msg);
                    sender().tell(new Message.SelectToDoAns(msg.getPage(), msg.getCountPerPage(), list), self());
                })
                .match(Message.FinishToDoReq.class, msg -> {
                    log.info("Receive Message. FinishToDoReq: {}", msg.getId());
                    Message.FinishToDoAns ans = finishTo(msg.getId());
                    sender().tell(ans, self());
                })
                .match(Message.UpdateToDoReq.class, msg -> {
                    log.info("Receive Message. UpdateToDoReq: {}", msg.getId());
                    Message.UpdateToDoAns ans = updateToDo(msg);
                    sender().tell(ans, self());
                })
                .matchAny(o -> {
                    log.info(o.toString());
                    getContext().getSystem().scheduler().scheduleOnce(Duration.ofSeconds(3), sender(), o, getContext().dispatcher(), self());
                })
                .build();
    }

    private IgniteCache getCache() {
        return getCache(toDoTableName, schema);
    }

    private IgniteCache getCache(final String tableName, final String schema) {
        log.info("Create or get Ignite Cache: [Table Name: {}][Schema: {}]", tableName, schema);
        return ignite.getOrCreateCache(
                new CacheConfiguration<>()
                        .setName(tableName)
                        .setSqlSchema(schema)
        );
    }

    private void initIgniteInMemoryDatabase(final boolean initSample) {
        log.info("startIgniteInMemoryDatabase");

        IgniteCache cache = getCache();

        // create table
        cache.query(createToDoTableQuery);
        log.info("Create Table: {}", createToDoTableQuery.getSql());

        cache.query(createRefTableQuery);
        log.info("Create Table: {}", createRefTableQuery.getSql());

        if (initSample) {
            // tell start to insert sample to database
            self().tell(new ProcessMessage.StartInsertIgniteSample(sampleToReqList), self());
        }
    }

    // (id, to Do, refList, registerDate, modifyDate, isDone)
    private Message.InsertToDoAns insertToDo(final Message.InsertToDoReq req) throws JsonProcessingException {
        log.debug("insertToDo");

        IgniteCache cache = getCache();

        final int id = ++idGenerator;
        final String toDo = req.getToDo();
        final String refList = listToJsonString(req.getRefList());
        final Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        try {
            cache.query(insertToDoQuery.setArgs(
                    id, toDo, refList, now, now, false));
            log.info(insertToDoQuery.getSql());

            if (!req.getRefList().isEmpty()) {
                for (final int ref : req.getRefList()) {
                    cache.query(insertRefQuery.setArgs(ref, id, false));
                    log.info(insertRefQuery.getSql());
                }
            }

            totalCount++;
            return new Message.InsertToDoAns(new ToDo(id, toDo, req.getRefList(), now, now, 0), totalCount);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    // (id, to Do, refList, registerDate, modifyDate, isDone)
    private List<ToDo> selectToDo(final Message.SelectToDoReq req) throws IOException {
        log.debug("selectToDo");

        final IgniteCache cache = getCache();
        final int offset = (req.getPage() - 1) * req.getCountPerPage();
        if(offset >= totalCount)
            return Collections.EMPTY_LIST;

        final FieldsQueryCursor<List<?>> cursor = cache.query(selectToDoQuery.setArgs(req.getCountPerPage(), offset));
        log.info("selectToDoQuery: {}", selectToDoQuery.getSql());

        final Iterator<List<?>> iterator = cursor.iterator();

        final List<ToDo> list = new ArrayList<>();
        while (iterator.hasNext()) {
            final List<?> row = iterator.next();

            final int id = Integer.class.cast(row.get(0));
            final String toDo = String.class.cast(row.get(1));
            final String refListString = String.class.cast(row.get(2));
            final List<Integer> refList = jsonStringToList(refListString);
            final Timestamp registerDate = Timestamp.class.cast(row.get(3));
            final Timestamp modifyDate = Timestamp.class.cast(row.get(4));
            final boolean isDone = Boolean.class.cast(row.get(5));

            ToDo toDoObject = new ToDo();
            toDoObject.setId(id);
            toDoObject.setToDo(toDo);
            toDoObject.setRefList(refList);
            toDoObject.setRegisterDate(registerDate);
            toDoObject.setModifyDate(modifyDate);
            toDoObject.setIsDone(isDone ? 1 : 0);

            list.add(toDoObject);
        }

        return list;
    }

    private Message.FinishToDoAns finishTo(final int id) {
        log.debug("finishTo");

        final IgniteCache cache = getCache();
        if (!canFinish(cache, id)) {
            return new Message.FinishToDoAns(id, false, "참조 된 일이 남아 있어서 완료 할 수 없습니다.");
        } else {
            int updateCount = 0;
            try {
                updateCount += updateToDoIsDone(cache, id, true);
                updateCount += updateRefIsDone(cache, id, true);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                // roll back
                if (updateCount > 0) {
                    updateToDoIsDone(cache, id, false);
                }
                return new Message.FinishToDoAns(id, false, "업데이트에서 문제가 발생했습니다.");
            }

            return new Message.FinishToDoAns(id, true, "");
        }
    }

    private Message.UpdateToDoAns updateToDo(final Message.UpdateToDoReq req) {
        log.debug("updateToDo");

        final IgniteCache cache = getCache();
        try {
            cache.query(updateToDoToDoQuery.setArgs(req.getToDo(), Timestamp.valueOf(LocalDateTime.now()), req.getId()));
            log.info("updateToDoToDoQuery: {}", updateToDoToDoQuery.getSql());
            return new Message.UpdateToDoAns(req.getId(), true, "");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return new Message.UpdateToDoAns(req.getId(), false, "할일 업데이트를 실패하였습니다.");
        }
    }

    private boolean canFinish(final IgniteCache cache, final int id) {
        final FieldsQueryCursor<List<?>> cursor = cache.query(selectRefByIdQuery.setArgs(id));
        final Iterator<List<?>> iterator = cursor.iterator();
        boolean result = false;
        while (iterator.hasNext())  {
            final List<?> row = iterator.next();
            final boolean isDone = Boolean.class.cast(row.get(2));
            result = true;
            if (isDone)
                continue;

            final int ref = Integer.class.cast(row.get(1));
            log.info("Cannot finish job cause {} Job is not finished.", ref);
            return false;
        }

        return result;
    }

    private int updateToDoIsDone(final IgniteCache cache, final int id, final boolean isDone) {
        cache.query(updateToDoIsDoneQuery.setArgs(isDone, Timestamp.valueOf(LocalDateTime.now()), id));
        log.info("updateToDoIsDoneQuery: {}", updateToDoIsDoneQuery.getSql());
        return 1;
    }

    private int updateRefIsDone(final IgniteCache cache, final int id, final boolean isDone) {
        cache.query(updateRefIsDoneQuery.setArgs(isDone, id));
        log.info("updateRefIsDoneQuery: {}", updateRefIsDoneQuery.getSql());
        return 1;
    }
}
