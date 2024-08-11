package com.youyk.test.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec11.AccountBalance;
import com.youyk.models.sec11.BalanceCheckRequest;
import com.youyk.models.sec11.Money;
import com.youyk.models.sec11.WithdrawRequest;
import com.youyk.test.common.ResponseObserver;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec02ServerStreamingDeadlineTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec02ServerStreamingDeadlineTest.class);

    @Test
    public void blockingDeadlineTest1(){
        WithdrawRequest request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();

        /**
         * server stream에서 2초의 dead line를 걸었다.
         * 하지만 실행해보면 데이터를 받고 있음에도 2초 뒤에 에러가 나는 상황이 발생함
         * 그래서 deadline를 사용할 때 server stream이면 조심해야 한다.
         */
        Iterator<Money> iterator = this.bankBlockingStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .withdraw(request);

        while(iterator.hasNext()){
            log.info("received money : {}", iterator.next());
        }

    }

    @Test
    public void blockingDeadlineTest2(){
        try{
            WithdrawRequest request = WithdrawRequest.newBuilder()
                    .setAccountNumber(1)
                    .setAmount(50)
                    .build();

            /**
             * blockingDeadlineTest1과 다르게 에러가 나지 않음 - try catch로 감싸서 에러를 안 나게 함 하지만 이건 문제가 있다.
             * 따라서 DeadlineBankService에서 Context.current().isCancelled() 조건을 추가해서 catch가 발동하면 멈추게 했ㅇㅁ
             */
            Iterator<Money> iterator = this.bankBlockingStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .withdraw(request);

            while(iterator.hasNext()){
                log.info("received money : {}", iterator.next());
            }
        }catch(Exception e){
            log.info("error");
        }

        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

    }

    @Test
    public void asyncDeadlineTest(){
        ResponseObserver<Money> observer = ResponseObserver.create();
        WithdrawRequest reuqest = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();

        this.bankStub
                .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .withdraw(reuqest, observer);

        observer.await();

        Assertions.assertEquals(2, observer.getItems().size());
        Assertions.assertEquals(Code.DEADLINE_EXCEEDED, Status.fromThrowable(observer.getThrowable()).getCode());
    }

}
