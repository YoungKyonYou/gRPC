package org.example.sec06;



import com.google.protobuf.Empty;
import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.AllAccountsResponse;
import com.youyk.models.sec06.BalanceCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Lec01UnaryBlockingClientTest extends AbstractTest{
    private static final Logger log = LoggerFactory.getLogger(Lec01UnaryBlockingClientTest.class);

    @Test
    public void getBalanceTest(){
        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        AccountBalance balance = this.blockingStub.getAccountBalance(request);
        log.info("unary balance received: {}", balance);
        Assertions.assertEquals(100, balance.getBalance());
    }

    @Test
    public void getAllAccountsTest(){
        AllAccountsResponse allAccounts = this.blockingStub.getAllAccounts(Empty.newBuilder().build());
        log.info("unary allAccounts received: {}", allAccounts);

        Assertions.assertEquals(10, allAccounts.getAccountsCount());
    }

}
