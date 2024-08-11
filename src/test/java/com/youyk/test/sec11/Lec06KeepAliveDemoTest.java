package com.youyk.test.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec11.AccountBalance;
import com.youyk.models.sec11.BalanceCheckRequest;
import com.youyk.models.sec11.BankServiceGrpc;
import com.youyk.test.common.AbstractChannelTest;
import java.util.concurrent.TimeUnit;
import org.example.common.GrpcServer;
import org.example.common.GrpcServerSec11;
import org.example.sec11.DeadlineBankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec06KeepAliveDemoTest extends AbstractChannelTest {
    private static final Logger log = LoggerFactory.getLogger(Lec06KeepAliveDemoTest.class);
    private final GrpcServerSec11 grpcServer = GrpcServerSec11.create(new DeadlineBankService());
    private BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

    /**
     * It is a class to demo the keep alive PING & GO AWAY
     */
    @BeforeAll
    public void setup(){
        this.grpcServer.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    public void stop(){
        this.grpcServer.stop();
    }

    /**
     * Configure the server with keep alive
     */
    @Test
    public void lazyChannelDemo(){
        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);

        AccountBalance response = this.bankBlockingStub.getAccountBalance(request);
        log.info("{}", response);

        //just blokcing the thread for 30 seconds;
        Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);
    }
}
