package com.youyk.test.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec11.AccountBalance;
import com.youyk.models.sec11.BalanceCheckRequest;
import com.youyk.models.sec11.BankServiceGrpc;
import com.youyk.test.common.AbstractChannelTest;
import java.util.concurrent.TimeUnit;
import org.example.common.GrpcServer;
import org.example.sec11.DeadlineBankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec04LazyChannelDemoTest extends AbstractChannelTest {
    private static final Logger log = LoggerFactory.getLogger(Lec04LazyChannelDemoTest.class);
    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
    private BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

    //서버는 Lazily 하게 생성된다
    //즉 grpcServer.start()를 주석처리하고 아래 Uninterruptibles 가 실행되고 나서 요청을 보내면 에러가 남 그래서 Lazy하게 생성된다고 표현
    @BeforeAll
    public void setup(){
        this.grpcServer.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    public void stop(){
        this.grpcServer.stop();
    }

    @Test
    public void lazyChannelDemo(){
        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);

        AccountBalance response = this.bankBlockingStub.getAccountBalance(request);
        log.info("{}", response);
    }
}
