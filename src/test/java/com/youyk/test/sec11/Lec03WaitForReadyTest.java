package com.youyk.test.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec11.BankServiceGrpc;

import com.youyk.models.sec11.Money;
import com.youyk.models.sec11.WithdrawRequest;
import com.youyk.test.common.AbstractChannelTest;
import io.grpc.Deadline;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.example.common.GrpcServer;
import org.example.sec11.DeadlineBankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec03WaitForReadyTest extends AbstractChannelTest {
    private static final Logger log = LoggerFactory.getLogger(Lec03WaitForReadyTest.class);
    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
    private BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

    @BeforeAll
    public void setup(){
       // this.grpcServer.start();
        Runnable runnable = () -> {
            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
            this.grpcServer.start();
        };

        Thread thread = new Thread(runnable);
        thread.start();

        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    public void stop(){
        this.grpcServer.stop();
    }

    @Test
    public void blockingDeadlineTest1(){
        log.info("sending the request");
        WithdrawRequest request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();


        //withWaitForReady()를 통해 서버가 켜지기까지 기다리게 할 수 있다.
        Iterator<Money> iterator = this.bankBlockingStub.withWaitForReady()
                .withdraw(request);

        while(iterator.hasNext()){
            log.info("{}", iterator.next());
        }

    }

    @Test
    public void blockingDeadlineTest2(){
        log.info("sending the request");
        WithdrawRequest request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();


        //withWaitForReady()를 통해 서버가 켜지기까지 기다리게 할 수 있다. withDeadline()를 통해 timeout을 15초만 설정해서 서버가 켜지지 않으면 에러가 발생한다.
        Iterator<Money> iterator = this.bankBlockingStub.withWaitForReady()
                .withDeadline(Deadline.after(15, TimeUnit.SECONDS))
                .withdraw(request);

        while(iterator.hasNext()){
            log.info("{}", iterator.next());
        }

    }

}
