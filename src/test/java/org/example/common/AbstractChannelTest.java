package org.example.common;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

/*
 @TestInstance(TestInstance.Lifecycle.PER_CLASS)를 사용하면,
 JUnit은 테스트 클래스당 하나의 인스턴스만 생성합니다.
 이는 모든 테스트 메소드가 같은 테스트 클래스 인스턴스를 공유하게 됨을 의미합니다.
  이 설정은 테스트 간에 상태를 공유해야 하는 경우나, @BeforeAll 및
  @AfterAll 메소드를 non-static으로 만들어야 하는 경우에 유용합니다.

  예시) Lec01UnaryBlockingClientTest 가 돌때 이것도 같이 돈다
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractChannelTest {

    protected ManagedChannel channel;

    //각 테스트마다 세팅
    @BeforeAll
    public void setupChannel(){
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
    }

    //각 테스트 후 중지
    @AfterAll
    public void stopChannel() throws InterruptedException {
        this.channel.shutdownNow()
                .awaitTermination(5, TimeUnit.SECONDS);
    }
}
