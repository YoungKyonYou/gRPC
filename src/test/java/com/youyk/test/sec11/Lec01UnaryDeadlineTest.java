package com.youyk.test.sec11;

import com.youyk.models.sec11.AccountBalance;
import com.youyk.models.sec11.BalanceCheckRequest;

import com.youyk.test.common.ResponseObserver;
import com.youyk.test.sec11.AbstractTest;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01UnaryDeadlineTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01UnaryDeadlineTest.class);

    @Test
    public void blockingDeadlineTest(){
        StatusRuntimeException exx = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(7)
                    .build();

            //2초 동안만 기다리고 초과하면 exception을 던짐
            AccountBalance response = this.bankBlockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .getAccountBalance(request);


        });

        Assertions.assertEquals(Code.DEADLINE_EXCEEDED, exx.getStatus().getCode());

    }

    @Test
    public void asyncDeadlineTest(){
        //ResponseObserver를 만들어서 비동기로 요청을 보냄
        ResponseObserver<AccountBalance> observer = ResponseObserver.create();
        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        //비동기 요청
        this.bankStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                                .getAccountBalance(request,observer);

        //countDownLatch await로 비동기를 기다림
        observer.await();

        Assertions.assertTrue(observer.getItems().isEmpty());
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(observer.getThrowable()).getCode());
    }

}
