package org.example.sec06;



import com.youyk.models.sec06.*;
import org.example.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;


public class Lec03ServerStreamingClientTest extends AbstractTest{
    private static final Logger log = LoggerFactory.getLogger(Lec03ServerStreamingClientTest.class);

    @Test
    public void blockingClientWithdrawTest() throws InterruptedException {
        WithdrawReques request = WithdrawReques.newBuilder()
                .setAccountNumber(2)
                .setAmount(20)
                .build();
        Iterator<Money> iterator = this.bankBlockingStub.withdraw(request);
        int count = 0;
        while(iterator.hasNext()){
            log.info("received money : {}", iterator.next());
            count++;
        }
        Assertions.assertEquals(2, count);
    }

    @Test
    public void asyncClientWithdrawTest() {
        WithdrawReques request = WithdrawReques.newBuilder()
                .setAccountNumber(2)
                .setAmount(20)
                .build();

        ResponseObserver<Money> observer = ResponseObserver.create();
        this.bankStub.withdraw(request, observer);
        observer.await();
        Assertions.assertEquals(2, observer.getItems().size());
        Assertions.assertEquals(10, observer.getItems().get(0).getAmount());
        Assertions.assertNull(observer.getThrowable());
    }

}
