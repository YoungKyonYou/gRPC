package com.youyk.test.sec08;

import com.vinsguru.models.sec08.GuessNumberGrpc;
import com.vinsguru.models.sec08.GuessRequest;
import com.vinsguru.models.sec08.GuessResponse;
import com.vinsguru.models.sec08.Result;
import com.youyk.test.common.AbstractChannelTest;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ThreadLocalRandom;
import org.example.common.GrpcServer;
import org.example.sec08.GuessNumberService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    It is simply a demo class
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GuessANumberTest extends AbstractChannelTest {

    private static final Logger log = LoggerFactory.getLogger(GuessANumberTest.class);
    private final GrpcServer server = GrpcServer.create(new GuessNumberService());
    private GuessNumberGrpc.GuessNumberStub stub;

    @BeforeAll
    public void setup(){
        this.server.start();
        this.stub = GuessNumberGrpc.newStub(channel);
    }

    @RepeatedTest(1)
    public void guessANumberGame(){
        var responseObserver = new GuessResponseHandler();
        /**
         * 메소드는 서버에 연결하고 클라이언트의 요청을 처리할 준비를 합니다.
         * 이 메소드는 StreamObserver<GuessRequest>를 반환하는데,
         * 이것은 클라이언트가 서버에 메시지를 보낼 수 있는 방법을 제공합니다.
         */
        var requestObserver = this.stub.makeGuess(responseObserver);
        responseObserver.setRequestObserver(requestObserver);
        /**
         * responseObserver.start()는 실제로 클라이언트가 서버에 첫 번째 요청을 보내는 것을 시작합니다.
         * 이 메소드는 GuessResponseHandler
         * 클래스에서 정의되어 있으며,
         * 이 메소드를 호출하면 send 메소드가 호출되어 실제로 서버에 요청을 보냅니다.
         */
        responseObserver.start();
        responseObserver.await();
        log.info("--------------");
    }

    @AfterAll
    public void stop(){
        this.server.stop();
    }

}
