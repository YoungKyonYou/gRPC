package org.example.sec10;

import com.google.common.util.concurrent.Uninterruptibles;

import com.youyk.models.sec10.AccountBalance;
import com.youyk.models.sec10.BalanceCheckRequest;
import com.youyk.models.sec10.BankServiceGrpc;
import com.youyk.models.sec10.Money;
import com.youyk.models.sec10.WithdrawRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import org.example.sec10.repository.AccountRepository;
import org.example.sec10.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        Optional<StatusRuntimeException> statusRuntimeException = RequestValidator.validateAccount(
                request.getAccountNumber());

        if(statusRuntimeException.isPresent()){
            responseObserver.onError(statusRuntimeException.get());
        }else{
            sendAccountBalance(request, responseObserver);
        }
             /*   .ifPresentOrElse(
                        responseObserver::onError,
                        () -> sendAccountBalance(request, responseObserver)
                );*/
    }

    private void sendAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .or(() -> RequestValidator.isAmountDivisibleBy10(request.getAmount()))
                .or(() -> RequestValidator.hasSufficientBalance(request.getAmount(), AccountRepository.getBalance(request.getAccountNumber())))
                .ifPresentOrElse(
                        responseObserver::onError,
                        () -> sendMoney(request, responseObserver)
                );
    }

    private void sendMoney(WithdrawRequest request, StreamObserver<Money> responseObserver) {
     //   try{
            var accountNumber = request.getAccountNumber();
            var requestedAmount = request.getAmount();
            for (int i = 0; i < (requestedAmount / 10); i++) {
                var money = Money.newBuilder().setAmount(10).build();
                responseObserver.onNext(money);
                log.info("money sent {}", money);
                AccountRepository.deductAmount(accountNumber, 10);
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            }
            responseObserver.onCompleted();
/*        }catch(Exception e){
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }*/

    }

}
