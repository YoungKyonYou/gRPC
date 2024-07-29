package org.example.sec06;

import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.BalanceCheckRequest;
import com.youyk.models.sec06.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class GrpcClient {
    private static final Logger log = LoggerFactory.getLogger(GrpcClient.class);

    public static void main(String[] args) throws InterruptedException {
        /*
        usePlaintext() 메소드를 호출하면 클라이언트가 서버와 통신할 때 SSL/TLS를 사용하지 않고 평문(plaintext)으로 데이터를 전송하게 됩니다.
        그러나 이 메소드는 보안이 중요하지 않은 상황에서만 사용해야 합니다. 실제 운영 환경에서는 데이터 보안을 위해 SSL/TLS를 사용하는 것이 좋습니다.
         */
        //channel should be mostly private.
        //we create once in the beginnin of the application
        //It is "managed"
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        //fake service
        //stub uses channel. Create once and inject whereever we need
        //Singleton / @Bean
        //newStub는 비동기, newBlockingStub는 동기에 씀
        //newStub은 유일하게 4가지 커뮤니케이션(Unary, Server Streaming, Client Streaming, BiDirectional Streaming)을 지원
        //blocking stub과 Future Stub은 Unary와 Server Streaming만 지원 함
        // It is thread safe
        BankServiceGrpc.BankServiceStub stub = BankServiceGrpc.newStub(channel);

        //Java Future용
       // BankServiceGrpc.newFutureStub(channel);


        //synchronous
       // AccountBalance balance = stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build());

        //Asynchronous
        stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build(),
                new StreamObserver<AccountBalance>(){
                    @Override
                    public void onNext(AccountBalance accountBalance) {
                        log.info("{}", accountBalance);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onCompleted() {
                        log.info("completed");
                    }
                });


        log.info("done");
        Thread.sleep(1000);
    }
}
