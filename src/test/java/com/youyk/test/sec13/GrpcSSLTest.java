package com.youyk.test.sec13;

import com.youyk.models.sec13.AccountBalance;
import com.youyk.models.sec13.BalanceCheckRequest;
import com.youyk.models.sec13.BankServiceGrpc;
import com.youyk.models.sec13.BankServiceGrpc.BankServiceBlockingStub;
import com.youyk.models.sec13.BankServiceGrpc.BankServiceStub;
import com.youyk.models.sec13.Money;
import com.youyk.models.sec13.WithdrawRequest;
import com.youyk.test.common.ResponseObserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcSSLTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(GrpcSSLTest.class);

    @Test
    public void clientWithSSLTest(){
/*        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
               // .usePlaintext() 이제 plain text를 사용하지 않습니다.
                .build();*/

        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 6565)
                .sslContext(clientSslContext())
                .build();

        BankServiceBlockingStub stub = BankServiceGrpc.newBlockingStub(channel);

        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        AccountBalance response = stub.getAccountBalance(request);

        log.info("{}", response);

        channel.shutdown();
    }

    @Test
    public void streaming() {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 6565)
                .sslContext(clientSslContext())
                .build();

        BankServiceStub stub = BankServiceGrpc.newStub(channel);
        WithdrawRequest request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(30)
                .build();
        ResponseObserver<Money> observer = ResponseObserver.<Money>create();

        stub.withdraw(request, observer);
        observer.await();

        channel.shutdownNow();
    }
}
