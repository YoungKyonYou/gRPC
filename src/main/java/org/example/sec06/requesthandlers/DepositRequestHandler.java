package org.example.sec06.requesthandlers;

import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.DepositRequest;
import io.grpc.stub.StreamObserver;
import org.example.sec06.BankService;
import org.example.sec06.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositRequestHandler implements StreamObserver<DepositRequest>{
    private static final Logger log = LoggerFactory.getLogger(DepositRequestHandler.class);
    private final StreamObserver<AccountBalance> responseObserver;
    private int accountNumber;

    public DepositRequestHandler(StreamObserver<AccountBalance> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(DepositRequest depositRequest) {
        switch(depositRequest.getRequestCase()){
            case ACCOUNT_NUMBER -> this.accountNumber = depositRequest.getAccountNumber();
            case MONEY -> AccountRepository.addAmount(this.accountNumber, depositRequest.getMoney().getAmount());
        }
    }

    @Override
    public void onError(Throwable t) {
        log.info("client error {}", t.getMessage());
    }

    /*
    responseObserver.onNext(accountBalance);는 클라이언트에게 데이터를 보내는 역할을 합니다.
    이 메소드를 호출하면, accountBalance 객체가 클라이언트에게 전송됩니다. 이 메소드는 여러 번 호출될 수 있으며,
     각 호출마다 새로운 데이터가 클라이언트에게 전송됩니다.  responseObserver.onCompleted();는
     서버가 클라이언트에게 모든 데이터를 전송했음을 알리는 역할을 합니다. 이 메소드를 호출하면, 클라이언트는
     더 이상 데이터를 기다리지 않고, 스트림을 종료합니다. 이 메소드는 한 번만 호출되어야 하며,
      호출 후에는 onNext나 onError를 호출하면 안 됩니다.  따라서,
      이 두 메소드는 서버가 클라이언트에게 데이터를 전송하고, 모든 데이터 전송이 완료되었음을 알리는 데 사용됩니다.
     */
    @Override
    public void onCompleted() {
        AccountBalance accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(this.accountNumber)
                .setBalance(AccountRepository.getBalance(this.accountNumber))
                .build();

        this.responseObserver.onNext(accountBalance);
        this.responseObserver.onCompleted();
    }
}
