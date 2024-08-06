package com.youyk.test.sec10;

import com.youyk.models.sec10.AccountBalance;
import com.youyk.models.sec10.BalanceCheckRequest;
import com.youyk.models.sec10.ValidationCode;
import io.grpc.StatusRuntimeException;

import org.example.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01UnaryInputValidationTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01UnaryInputValidationTest.class);

    @Test
    public void blockingInputValidationTest(){

        StatusRuntimeException ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(11)
                    .build();
            AccountBalance response = this.bankBlockingStub.getAccountBalance(request);
        });

        Assertions.assertEquals(ValidationCode.INVALID_ACCOUNT, getValidationCode(ex));




    }

/*    @Test
    public void asyncInputValidationTest(){
        BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(11)
                .build();
        ResponseObserver<AccountBalance> observer = ResponseObserver.create();
        this.bankStub.getAccountBalance(request, observer);
        observer.await();

        Assertions.assertTrue(observer.getItems().isEmpty());
        Assertions.assertNotNull(observer.getThrowable());
        Assertions.assertEquals(ValidationCode.INVALID_ACCOUNT, getValidationCode(observer.getThrowable()));
    }*/
}
