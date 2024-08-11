package com.youyk.test.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.BalanceCheckRequest;
import com.youyk.models.sec06.BankServiceGrpc;
import com.youyk.models.sec06.DepositRequest;
import com.youyk.models.sec06.Money;
import com.youyk.test.common.ResponseObserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    It is a class to demo nginx load balancing
    Ensure that you run the 2 bank service instance
    1. use sec06 bank service
    2. run 2 instances. 1 on port 6565 and other on 7575
    3. start nginx (src/test/resources). nginx listens on port 8585
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec07LoadBalancingDemoTest {

    private static final Logger log = LoggerFactory.getLogger(Lec07LoadBalancingDemoTest.class);
    private BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    private BankServiceGrpc.BankServiceStub bankStub;
    private ManagedChannel channel;

    @BeforeAll
    public void setup() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 8585)
                .usePlaintext()
                .build();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankStub = BankServiceGrpc.newStub(channel);
    }

    // I do not want to run this as part of mvn test
    @Test
    public void loadBalancingDemo() {
        for (int i = 1; i <= 10 ; i++) {
            BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();

            AccountBalance response = this.bankBlockingStub.getAccountBalance(request);
            log.info("{}", response);
        }
    }

    /**
     * 이 경우 BankInstance1과 BankInstance2를 실행하고 이 테스트를 실행하면 두 인스턴스에 요청이 균등한게 아니라
     * 하나의 서버에만 요청이 가는 것을 확인할 수 있다.
     * 하나의 스트리밍 request라고 생각할 것
     */
    @Test
    public void loadBalancingDemoAsync() {

        ResponseObserver<AccountBalance> responseObserver = ResponseObserver.create();
        StreamObserver<DepositRequest> requestObserver = this.bankStub.deposit(responseObserver);

        // initial message - account number
        IntStream.rangeClosed(1, 30)
                .mapToObj(i -> Money.newBuilder().setAmount(10).build())
                .map(m -> DepositRequest.newBuilder().setMoney(m).build())
                .forEach(d -> {
                        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                        requestObserver.onNext(d);
                });

        //notify the server that we are done
        requestObserver.onCompleted();

        //at this point out response observer should receive a response
        responseObserver.await();

    }

    @AfterAll
    public void stop() {
        this.channel.shutdownNow();
    }

}
