package org.example.sec06.requesthandlers;

import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.TransferRequest;
import com.youyk.models.sec06.TransferResponse;
import com.youyk.models.sec06.TransferStatus;
import io.grpc.stub.StreamObserver;
import org.example.sec06.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferRequestHandler implements StreamObserver<TransferRequest>{
    private static final Logger log = LoggerFactory.getLogger(TransferRequestHandler.class);
    private final StreamObserver<TransferResponse> responseObserver;

    public TransferRequestHandler(StreamObserver<TransferResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        TransferStatus status = this.transfer(transferRequest);
        //메시지를 줄때마다 서버가 그거에 대한 응답을 무조건 줄 필요는 없다
        //아래와 같이 COMPLETED일때만 응답을 줄 수 있다
/*        if(TransferStatus.COMPLETED.equals(status)){
            TransferResponse response = TransferResponse.newBuilder()
                    .setFromAccount(this.toAccountBalance(transferRequest.getFromAccount()))
                    .setToAccount(this.toAccountBalance(transferRequest.getToAccount()))
                    .setStatus(status)
                    .build();
            this.responseObserver.onNext(response);
        }*/

        TransferResponse response = TransferResponse.newBuilder()
                .setFromAccount(this.toAccountBalance(transferRequest.getFromAccount()))
                .setToAccount(this.toAccountBalance(transferRequest.getToAccount()))
                .setStatus(status)
                .build();
        this.responseObserver.onNext(response);

    }

    @Override
    public void onError(Throwable t) {
        log.info("client error {}", t.getMessage());
    }

    @Override
    public void onCompleted() {
        log.info("transfer request stream completed");
        this.responseObserver.onCompleted();
    }

    private TransferStatus transfer(TransferRequest request){
        int amount = request.getAmount();
        int fromAccount = request.getFromAccount();
        int toAccount = request.getToAccount();
        TransferStatus status = TransferStatus.REJECTED;

        if(AccountRepository.getBalance(fromAccount)  >= amount && (fromAccount != toAccount)){
            //계좌에서 출금
            AccountRepository.deductAmount(fromAccount, amount);
            //계좌에 이체
            AccountRepository.addAmount(toAccount, amount);
            status = TransferStatus.COMPLETED;
        }
        return status;
    }

    private AccountBalance toAccountBalance(int accountNumber){
        return AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber))
                .build();
    }
}


