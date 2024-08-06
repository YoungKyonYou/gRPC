package org.example.sec09;


import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec09.Money;

import com.youyk.models.sec09.AccountBalance;
import com.youyk.models.sec09.BalanceCheckRequest;
import com.youyk.models.sec09.BankServiceGrpc;

import com.youyk.models.sec09.WithdrawRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import org.example.sec09.repository.AccountRepository;
import org.example.sec09.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankService extends BankServiceGrpc.BankServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    //이건 service 클래스이고 request와 response를 받고 보낸다
    //
    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .map(Status::asRuntimeException)
                .ifPresentOrElse(
                        responseObserver::onError,
                        () -> sendAccountBalance(request, responseObserver)
                );
    }

    private void sendAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {

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
    public void withdraw(WithdrawRequest request, StreamObserver<com.youyk.models.sec09.Money> responseObserver) {
        //validateAccount가 empty면 or이 trigger 됨
        RequestValidator.validateAccount(request.getAccountNumber())
                .or(()-> RequestValidator.isAmountDivisibleBy10(request.getAmount()))
                .or(() -> RequestValidator.hasSufficientBalance(request.getAmount(), AccountRepository.getBalance(request.getAccountNumber())))
                .map(Status::asRuntimeException)
                .ifPresentOrElse(
                        responseObserver::onError,
                        ()->sendMoney(request,responseObserver)
                );
    }

    private void sendMoney(WithdrawRequest request, StreamObserver<com.youyk.models.sec09.Money> responseObserver) {
          /*
            Ideally we should do some input validation. But we are going to assume only happy path scenarios.
            Because, in gRPC, there are multiple ways to communicate the error message to the client. It has to be discussed in detail separately.
            Assumption: account # 1 - 10 & withdraw amount is multiple of $10
         */
        int accountNumber = request.getAccountNumber();
        int requestedAmount = request.getAmount();

        for (int i = 0; i < requestedAmount / 10; i++) {
            Money money = Money.newBuilder().setAmount(10).build();
            responseObserver.onNext(money);
            log.info("money sent {}", money);

            org.example.sec06.repository.AccountRepository.deductAmount(accountNumber, 10);
            //현재 스레드를 지정된 시간 동안 일시 중지합니다. 이 경우에는 1초 동안 일시 중지합니다.
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        responseObserver.onCompleted();
    }



}
