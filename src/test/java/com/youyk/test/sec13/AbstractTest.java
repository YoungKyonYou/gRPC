package com.youyk.test.sec13;

import com.youyk.models.sec11.BankServiceGrpc;
import com.youyk.test.common.AbstractChannelTest;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.concurrent.Callable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.example.common.sec13.GrpcServer;
import org.example.sec11.DeadlineBankService;
import org.example.sec13.BankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public abstract class AbstractTest {
    private static final Path KEY_STORE = Path.of("src/test/resources/certs/grpc.keystore.jks");
    private static final Path TRUST_STORE = Path.of("src/test/resources/certs/grpc.truststore.jks");
    private static final char[] PASSWORD = "changeit".toCharArray();
    private final GrpcServer grpcServer = GrpcServer.create(6565, b -> {
       b.addService(new BankService())
               .sslContext(serverSslContext());
    });

    @BeforeAll
    public void start(){
        this.grpcServer.start();
    }

    @AfterAll
    public void stop(){
        this.grpcServer.stop();
    }

    //서버 측에서 SSL/TLS를 설정하기 위한 구성을 제공
    protected SslContext serverSslContext(){
        return handleException(() ->
                GrpcSslContexts.configure(SslContextBuilder.forServer(getKeyManagerFactory())).build());
    }

    //클라이언트 측에서 SSL/TLS를 설정하기 위한 구성을 제공
    protected SslContext clientSslContext(){
        return handleException(() ->
            GrpcSslContexts.configure(SslContextBuilder.forClient()).trustManager(getTrustManagerFactory()).build());
    }

    //서버가 사용할 인증서를 로드
    protected KeyManagerFactory getKeyManagerFactory() {
        return handleException(() -> {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE.toFile(), PASSWORD);
            kmf.init(keyStore, PASSWORD);
            return kmf;
        });

    }

    //클라이언트가 신뢰할 수 있는 인증서를 로드
    protected TrustManagerFactory getTrustManagerFactory() {
        return handleException(() -> {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore trustStore = KeyStore.getInstance(TRUST_STORE.toFile(), PASSWORD);
            tmf.init(trustStore);
            return tmf;
        });
    }

    private<T> T handleException(Callable<T> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
