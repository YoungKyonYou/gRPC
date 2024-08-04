package com.youyk.test.sec07;


import com.youyk.models.sec07.FlowControlServiceGrpc;
import com.youyk.models.sec07.RequestSize;
import com.youyk.test.common.AbstractChannelTest;
import io.grpc.stub.StreamObserver;
import org.example.common.GrpcServer;
import org.example.sec07.FlowControlService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlowControlTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new FlowControlService());
    private FlowControlServiceGrpc.FlowControlServiceStub stub;

    @BeforeAll
    public void setup(){
        this.server.start();
        this.stub = FlowControlServiceGrpc.newStub(this.channel);
    }

    @Test
    public void flowControl(){
        //TODO
        ResponseHandler responseObserver = new ResponseHandler();
        StreamObserver<RequestSize> requestObserver = this.stub.getMessages(responseObserver);
        responseObserver.setRequestObserver(requestObserver);
        responseObserver.start();
        responseObserver.await();
    }

    @AfterAll
    public void stop(){
        this.server.stop();
    }
}
