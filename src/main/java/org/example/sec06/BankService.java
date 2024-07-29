package org.example.sec06;

import com.google.protobuf.Empty;
import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.AllAccountsResponse;
import com.youyk.models.sec06.BalanceCheckRequest;
import com.youyk.models.sec06.BankServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.example.sec06.repository.AccountRepository;

import java.util.List;

public class BankService extends BankServiceGrpc.BankServiceImplBase {
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
}
