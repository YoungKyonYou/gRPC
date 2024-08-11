package com.youyk.test.sec09;

import com.youyk.models.sec09.BalanceCheckRequest;
import com.youyk.models.sec09.AccountBalance;
import com.youyk.models.sec10.ErrorMessage;
import io.grpc.Metadata.Key;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import java.util.Objects;
import org.example.common.ResponseObserver;
import org.example.sec09.BankService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01UnaryInputValidationTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01UnaryInputValidationTest.class);

/*    @Test
    public void blockingInputValidationTest(){

        StatusRuntimeException ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            BalanceCheckRequest request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(11)
                    .build();
            AccountBalance response = this.bankBlockingStub.getAccountBalance(request);
        });

        Assertions.assertEquals(Code.INVALID_ARGUMENT, ex.getStatus().getCode());
        Key<ErrorMessage> key = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());
        System.out.println(Objects.requireNonNull(ex.getTrailers().get(key)).getValidationCode());



    }*/

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
        Assertions.assertEquals(Code.INVALID_ARGUMENT, ((StatusRuntimeException)observer.getThrowable()).getStatus().getCode());
    }*/
}
