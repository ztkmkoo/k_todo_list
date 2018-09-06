package com.ztkmkoo.todolist.process.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.PatternsCS;
import akka.testkit.TestKit;
import com.ztkmkoo.todolist.common.message.Message;
import com.ztkmkoo.todolist.process.actor.message.ProcessMessage;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class MasterActorTest {

    private ActorSystem actorSystem;

    private final Ignite ignite = Ignition.start();

    @Before
    public void setUp() throws Exception {
        actorSystem = ActorSystem.create("TestKitDocTest");
    }

    @Test
    public void testInsertToDoReq() throws ExecutionException, InterruptedException {
        new TestKit(actorSystem) {{
            assertEquals("TestKitDocTest", actorSystem.name());

            final ActorRef actorRef = actorSystem.actorOf(MasterActor.props(ignite), "master");
            actorRef.tell(new ProcessMessage.StartMasterActor(false), ActorRef.noSender());

            final List<Message.InsertToDoReq> sampleToReqList = new ArrayList<>(Arrays.asList(
                    MasterActor.sampleInsertToDoReqCreator("집안일"),  // 1
                    MasterActor.sampleInsertToDoReqCreator("빨래", 1),    // 2
                    MasterActor.sampleInsertToDoReqCreator("청소", 1),    // 3
                    MasterActor.sampleInsertToDoReqCreator("방청소", Arrays.asList(1, 3)), // 4
                    MasterActor.sampleInsertToDoReqCreator("빨래감 종류별로 구별하기", Arrays.asList(1, 2)),   // 5
                    MasterActor.sampleInsertToDoReqCreator("세탁물 모두 건조실에 널기", Arrays.asList(1, 2)),  // 6
                    MasterActor.sampleInsertToDoReqCreator("음식하기 ", 1), // 7
                    MasterActor.sampleInsertToDoReqCreator("재료 씻고 다듬기", Arrays.asList(1, 7)) // 8
            ));

            for (final Message.InsertToDoReq req : sampleToReqList) {
                final CompletableFuture future = PatternsCS.ask(actorRef, req, Duration.ofSeconds(3)).toCompletableFuture();

                // success
                final Message.InsertToDoAns ans =  Message.InsertToDoAns.class.cast(future.get());
            }
        }};
    }

    @Test
    public void testSelectToDoReq() throws Exception {
        new TestKit(actorSystem) {{
            assertEquals("TestKitDocTest", actorSystem.name());

            final ActorRef actorRef = actorSystem.actorOf(MasterActor.props(ignite), "master");
            actorRef.tell(new ProcessMessage.StartMasterActor(true), ActorRef.noSender());

            // wait for all the sample insert to the ignite database
            Thread.sleep(3000);

            final int page = 2;
            final int countPerPage = 5;
            final Message.SelectToDoReq req = new Message.SelectToDoReq(page, countPerPage);
            final CompletableFuture future = PatternsCS.ask(actorRef, req, Duration.ofSeconds(3)).toCompletableFuture();
            final Message.SelectToDoAns ans  = Message.SelectToDoAns.class.cast(future.get());
            System.out.println("[" + ans.getToDoList().size() + "]" + ans.getToDoList());
            assert (ans.getToDoList().size() <= countPerPage) : "Error in get ToDo";
        }};
    }

    @Test
    public void testFinishToDoReq() throws Exception {
        new TestKit(actorSystem) {{
            assertEquals("TestKitDocTest", actorSystem.name());

            final ActorRef actorRef = actorSystem.actorOf(MasterActor.props(ignite), "master");
            actorRef.tell(new ProcessMessage.StartMasterActor(true), ActorRef.noSender());

            // wait for all the sample insert to the ignite database
            Thread.sleep(3000);

            final Message.FinishToDoReq req = new Message.FinishToDoReq(1);
            final CompletableFuture future = PatternsCS.ask(actorRef, req, Duration.ofSeconds(3)).toCompletableFuture();
            final Message.FinishToDoAns ans  = Message.FinishToDoAns.class.cast(future.get());

            assert (ans.getIsDone() == 1) : "Error in finish job: cause, " + ans.getErrorLog();
        }};
    }

    @Test
    public void testUpdateToDoReq() throws Exception {
        new TestKit(actorSystem) {{
            assertEquals("TestKitDocTest", actorSystem.name());

            final ActorRef actorRef = actorSystem.actorOf(MasterActor.props(ignite), "master");
            actorRef.tell(new ProcessMessage.StartMasterActor(true), ActorRef.noSender());

            // wait for all the sample insert to the ignite database
            Thread.sleep(3000);

            // 2: 빨래 > 빠알래.
            final Message.UpdateToDoReq req = new Message.UpdateToDoReq(2, "빠알래");
            final CompletableFuture future = PatternsCS.ask(actorRef, req, Duration.ofSeconds(3)).toCompletableFuture();
            final Message.UpdateToDoAns ans  = Message.UpdateToDoAns.class.cast(future.get());

            assert ans.isDone() : "Error in finish job: cause, " + ans.getErrorLog();
        }};
    }
}