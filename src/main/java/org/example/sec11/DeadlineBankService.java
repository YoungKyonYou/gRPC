package org.example.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec11.AccountBalance;
import com.youyk.models.sec11.BalanceCheckRequest;
import com.youyk.models.sec11.BankServiceGrpc;

import com.youyk.models.sec11.Money;
import com.youyk.models.sec11.WithdrawRequest;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import org.example.sec06.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadlineBankService extends BankServiceGrpc.BankServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(DeadlineBankService.class);

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

        /**
         * slow processing 실험을 위한 sleep
         */
        //현재 스레드를 지정된 시간 동안 일시 중지합니다. 이 경우에는 3초 동안 일시 중지합니다.
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }



    //streaming 순서는 하나하나 그대로 보내진다
    //StreamObserver는 Thread-safe하지 않다. 그래서 synchronizedList를 사용한다
    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
           /*
            Ideally we should do some input validation. But we are going to assume only happy path scenarios.
            Because, in gRPC, there are multiple ways to communicate the error message to the client. It has to be discussed in detail separately.
            Assumption: account # 1 - 10 & withdraw amount is multiple of $10
         */
        int accountNumber = request.getAccountNumber();
        int requestedAmount = request.getAmount();
        Integer accountBalance = AccountRepository.getBalance(accountNumber);

        if(requestedAmount > accountBalance){
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            return;
        }

        // gRPC의 라이프사이클을 관리하는 것이 Context이다.
        // Context.current().isCancelled()는 현재의 gRPC 호출이 취소되었는지 여부를 확인하는데 사용한다
        //Context는 호출의 수명 동안 유지되는 정보를 포함하며, 호출이 취소되었는지 여부,
        // 타임아웃이 발생했는지 여부 등의 상태 정보를 가지고 있습니다.
        for (int i = 0; i < requestedAmount / 10 && !Context.current().isCancelled(); i++) {
            Money money = Money.newBuilder().setAmount(10).build();
            responseObserver.onNext(money);
            log.info("money sent {}", money);

            AccountRepository.deductAmount(accountNumber, 10);
            //현재 스레드를 지정된 시간 동안 일시 중지합니다. 이 경우에는 1초 동안 일시 중지합니다.
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        log.info("streaming completed");
        responseObserver.onCompleted();
    }

}
