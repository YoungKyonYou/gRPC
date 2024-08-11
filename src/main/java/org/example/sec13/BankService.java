package org.example.sec13;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec13.AccountBalance;
import com.youyk.models.sec13.BalanceCheckRequest;
import com.youyk.models.sec13.BankServiceGrpc;
import com.youyk.models.sec13.Money;
import com.youyk.models.sec13.WithdrawRequest;
import io.grpc.Context;
import io.grpc.Status;

import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import org.example.sec13.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

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
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int requestedAmount = request.getAmount();

        Integer accountBalance = AccountRepository.getBalance(accountNumber);

        if (requestedAmount > accountBalance) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            return;
        }

        for (int i = 0; i < (requestedAmount / 10) && !Context.current().isCancelled(); i++) {
            Money money = Money.newBuilder().setAmount(10).build();
            responseObserver.onNext(money);
            log.info("money sent {}", money);
            AccountRepository.deductAmount(accountNumber, 10);
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        log.info("streaming completed");
        responseObserver.onCompleted();
    }



}
