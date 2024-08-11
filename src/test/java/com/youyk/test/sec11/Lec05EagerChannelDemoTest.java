package com.youyk.test.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec11.AccountBalance;
import com.youyk.models.sec11.BalanceCheckRequest;
import com.youyk.models.sec11.BankServiceGrpc;
import com.youyk.test.common.AbstractChannelTest;
import java.util.concurrent.TimeUnit;
import org.example.common.GrpcServer;
import org.example.sec11.DeadlineBankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec05EagerChannelDemoTest extends AbstractChannelTest {
    /*
    It is a class to demo the eager channel creation behavior.
    There is a bug: https://github.com/grpc/grpc-java/issues/10517
 */
    private static final Logger log = LoggerFactory.getLogger(Lec05EagerChannelDemoTest.class);

    //Lec04에서 Lazy하게 생성되었던 것을 Eager하게 생성한다.
    @Test
    public void lazyChannelDemo(){
        //false를 넘겨주면 현재 상태를 받는다.
        //true면 현재 상태를 받고 채널을 열어준다. 하지만 위 명시된 것과 같이 버그 때문에 안됨
        log.info("{}", channel.getState(false));
    }
}
