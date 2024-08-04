package com.youyk.test.sec07;

import com.google.common.util.concurrent.Uninterruptibles;
import com.youyk.models.sec07.Output;
import com.youyk.models.sec07.RequestSize;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.example.sec07.FlowControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler implements StreamObserver<Output> {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    /*
    CountDownLatch는 gRPC 서비스의 비동기 응답을 기다리는 데 사용됩니다.
     ResponseHandler 클래스는 gRPC 서비스의 응답을 처리하는
     StreamObserver를 구현하며, onNext(), onError(), onCompleted() 메소드에서 countDown()을 호출하여
     카운트를 감소시킵니다.
     await() 메소드는 테스트 메소드에서 호출되어 gRPC 서비스의 응답이 모두 도착할 때까지 기다립니다.
     */
    private StreamObserver<RequestSize> requestObserver;
    private int size ;

    @Override
    public void onNext(Output output) {
        //서버가 보내는 응답 카운트다운
        this.size--;
        process(output);
        if(this.size == 0){
            log.info("------------");
            //1~5까지 랜덤 int 발생
            this.request(ThreadLocalRandom.current().nextInt(1, 6));
           // this.request(3);
        }
    }

    @Override
    public void onError(Throwable t) {
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        //서버가 더이상 보내줄 응답이 없으니 requestObserver를 close한다.
        this.requestObserver.onCompleted();
        log.info("completed");
        latch.countDown();
    }

    public void setRequestObserver(StreamObserver<RequestSize> requestObserver) {
        this.requestObserver = requestObserver;
    }

    private void request(int size){
        log.info("requesting size {}", size);
        this.size = size;
        //서버하네 size만큼 응답을 달라고 요청하는 것
        this.requestObserver.onNext(RequestSize.newBuilder().setSize(size).build());
    }

    private void process(Output output){
        log.info("received {}", output);
        Uninterruptibles.sleepUninterruptibly(ThreadLocalRandom.current().nextInt(50, 200), TimeUnit.MILLISECONDS);
    }

    public void await(){
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(){
        //요청을 보내기
        this.request(3);
    }
}
