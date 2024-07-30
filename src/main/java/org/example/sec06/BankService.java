package org.example.sec06;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import com.youyk.models.sec06.*;
import io.grpc.stub.StreamObserver;
import org.example.sec05.V2VersionCompatibility;
import org.example.sec06.repository.AccountRepository;
import org.example.sec06.requesthandlers.DepositRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    //이건 service 클래스이고 request와 response를 받고 보낸다
    //
    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        Integer balance = AccountRepository.getBalance(accountNumber);

        AccountBalance accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();

        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllAccounts(Empty request, StreamObserver<AllAccountsResponse> responseObserver) {
        List<AccountBalance> accounts = AccountRepository.getAllAccounts()
                .entrySet()
                .stream()
                .map(e -> AccountBalance.newBuilder()
                        .setAccountNumber(e.getKey())
                        .setBalance(e.getValue())
                        .build())
                .toList();

        AllAccountsResponse response = AllAccountsResponse.newBuilder()
                .addAllAccounts(accounts).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //streaming 순서는 하나하나 그대로 보내진다
    //StreamObserver는 Thread-safe하지 않다. 그래서 synchronizedList를 사용한다
    @Override
    public void withdraw(WithdrawReques request, StreamObserver<Money> responseObserver) {
           /*
            Ideally we should do some input validation. But we are going to assume only happy path scenarios.
            Because, in gRPC, there are multiple ways to communicate the error message to the client. It has to be discussed in detail separately.
            Assumption: account # 1 - 10 & withdraw amount is multiple of $10
         */
        int accountNumber = request.getAccountNumber();
        int requestedAmount = request.getAmount();
        Integer accountBalance = AccountRepository.getBalance(accountNumber);

        if(requestedAmount > accountBalance){
            responseObserver.onCompleted();
            return;
        }

        for (int i = 0; i < requestedAmount / 10; i++) {
            Money money = Money.newBuilder().setAmount(10).build();
            responseObserver.onNext(money);
            log.info("money sent {}", money);

            AccountRepository.deductAmount(accountNumber, 10);
            //현재 스레드를 지정된 시간 동안 일시 중지합니다. 이 경우에는 1초 동안 일시 중지합니다.
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        responseObserver.onCompleted();
    }

    //StreamObserver<AccountBalance> responseObserver는 outgoing message이고
    //StreamObserver<DepositRequest> requestObserver는 incomming message이다.
    @Override
    public StreamObserver<DepositRequest> deposit(StreamObserver<AccountBalance> responseObserver) {
        return new DepositRequestHandler(responseObserver);
    }
}
