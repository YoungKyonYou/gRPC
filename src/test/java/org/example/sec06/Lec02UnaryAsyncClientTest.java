package org.example.sec06;



import com.google.protobuf.Empty;
import com.youyk.models.sec06.AccountBalance;
import com.youyk.models.sec06.AllAccountsResponse;
import com.youyk.models.sec06.BalanceCheckRequest;
import org.example.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Lec02UnaryAsyncClientTest extends AbstractTest{
    private static final Logger log = LoggerFactory.getLogger(Lec02UnaryAsyncClientTest.class);

    @Test
    public void getBalanceTest() throws InterruptedException {
        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        ResponseObserver<AccountBalance> observer = ResponseObserver.create();
        this.bankStub.getAccountBalance(request, observer);
        observer.await();
        Assertions.assertEquals(1, observer.getItems().size());
        Assertions.assertEquals(100, observer.getItems().get(0).getBalance());
        Assertions.assertNull(observer.getThrowable());



    }

    @Test
    public void getAllAccountsTest(){
        ResponseObserver<AllAccountsResponse> observer = ResponseObserver.create();
        this.bankStub.getAllAccounts(Empty.newBuilder().build(), observer);
        observer.await();
        Assertions.assertEquals(1, observer.getItems().size());
        Assertions.assertEquals(10, observer.getItems().get(0).getAccountsCount());
        Assertions.assertNull(observer.getThrowable());


    }

/*    @Test
    public void getAllAccountsTest(){
        AllAccountsResponse allAccounts = this.blockingStub.getAllAccounts(Empty.newBuilder().build());
        log.info("unary allAccounts received: {}", allAccounts);

        Assertions.assertEquals(10, allAccounts.getAccountsCount());
    }*/

}
