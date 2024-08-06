package org.example.sec06;


import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.DepositRequest;
import com.youyk.models.sec06.Money;
import io.grpc.stub.StreamObserver;
import org.example.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;


public class Lec04ClientStreamingTest extends AbstractTest{
    private static final Logger log = LoggerFactory.getLogger(Lec04ClientStreamingTest.class);

    @Test
    public void depositTest()  {
        ResponseObserver<AccountBalance> responseObserver = ResponseObserver.create();
        //deposit는 gRPC 클라이언트 스트리밍 요청을 설정하는 부분
        //deposit 메소드는 클라이언트 스트리밍 RPC를 나타내며, 이는 클라이언트가 서버에 스트림으로 데이터를 보낼 수 있게 해줍니다.
        StreamObserver<DepositRequest> requestObserver = this.bankStub.deposit(responseObserver);

        //initial message - account number 1~10까지 account number 중에 5를 선택
        requestObserver.onNext(DepositRequest.newBuilder().setAccountNumber(5).build());

 /*       //코드는 현재 실행 중인 스레드를 1초 동안 일시 중지합니다
        //스레드가 인터럽트를 받아도 중단되지 않습니다. 즉, 이 메소드는 스레드가 지정된 시간 동안 무조건 대기하도록 보장합니다
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        //Client에서 Cancel Stream 하는 방법
        requestObserver.onError(new RuntimeException());*/


        IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Money.newBuilder().setAmount(10).build())
                .map(m -> DepositRequest.newBuilder().setMoney(m).build())
                .forEach(requestObserver::onNext);
        //notifying the server that we are done
        requestObserver.onCompleted();

        //at this point out response observer should receive a response
        responseObserver.await();

        Assertions.assertEquals(1, responseObserver.getItems().size());
        Assertions.assertEquals(200, responseObserver.getItems().get(0).getBalance());
        Assertions.assertNull(responseObserver.getThrowable());
    }



}
