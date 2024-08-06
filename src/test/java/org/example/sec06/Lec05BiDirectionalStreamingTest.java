package org.example.sec06;


import com.youyk.models.sec06.*;
import io.grpc.stub.StreamObserver;
import org.example.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;


public class Lec05BiDirectionalStreamingTest extends AbstractTest{
    private static final Logger log = LoggerFactory.getLogger(Lec05BiDirectionalStreamingTest.class);

    @Test
    public void depositTest()  {
        ResponseObserver<TransferResponse> responseObserver = ResponseObserver.create();
        StreamObserver<TransferRequest> requestObserver = this.transferStub.transfer(responseObserver);
        List<TransferRequest> requests = List.of(
                TransferRequest.newBuilder().setAmount(10).setFromAccount(6).setToAccount(6).build(),
                TransferRequest.newBuilder().setAmount(110).setFromAccount(6).setToAccount(7).build(),
                TransferRequest.newBuilder().setAmount(10).setFromAccount(6).setToAccount(7).build(),
                TransferRequest.newBuilder().setAmount(10).setFromAccount(7).setToAccount(6).build()
        );

        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();
        //코드는 클라이언트가 서버로부터 모든 응답을 받을 때까지 대기하는 역할을 합니다.
        //이렇게 하면 서버로부터 응답을 모두 받고 나서야 다음 코드를 실행하게 되므로, 응답을 처리하는 로직이 올바르게 수행될 수 있습니다.
        responseObserver.await();

        Assertions.assertEquals(4,responseObserver.getItems().size());
        this.validate(responseObserver.getItems().get(0), TransferStatus.REJECTED, 100, 100 );
        this.validate(responseObserver.getItems().get(1), TransferStatus.REJECTED, 100, 100 );
        this.validate(responseObserver.getItems().get(2), TransferStatus.COMPLETED, 90, 110 );
        this.validate(responseObserver.getItems().get(3), TransferStatus.COMPLETED, 100, 100 );
    }
    private void validate(TransferResponse response, TransferStatus status, int fromAccountBalance, int toAccountBalance){
        Assertions.assertEquals(status, response.getStatus());
        Assertions.assertEquals(fromAccountBalance, response.getFromAccount().getBalance());
        Assertions.assertEquals(toAccountBalance, response.getToAccount().getBalance());
    }
}
