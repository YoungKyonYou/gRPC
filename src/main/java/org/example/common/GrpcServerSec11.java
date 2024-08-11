package org.example.common;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.example.sec06.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcServerSec11 {

    private final Server server;

    private static final Logger log = LoggerFactory.getLogger(GrpcServerSec11.class);

    public GrpcServerSec11(Server server) {
        this.server = server;
    }

    public static GrpcServerSec11 create(BindableService... services){
        return create(6565, services);
    }

    public static GrpcServerSec11 create(int port, BindableService... services){
        /**
         * keepAliveTime(long timeout, TimeUnit unit): 클라이언트가 아무런 RPC를 생성하지 않고 있을 때, 서버가 클라이언트에게 keepalive ping을 보내는 시간을 설정합니다.
         * 이는 클라이언트가 여전히 연결되어 있는지 확인하는데 사용됩니다. 여기서는 10초로 설정되어 있습니다.
         *
         * keepAliveTimeout(long timeout, TimeUnit unit): 서버가 keepalive ping을 보낸 후, 클라이언트로부터 응답을 기다리는 시간을 설정합니다.
         * 이 시간이 지나도 응답이 없으면, 서버는 클라이언트와의 연결을 끊습니다. 여기서는 1초로 설정되어 있습니다.
         *
         * maxConnectionIdle(long timeout, TimeUnit unit): 클라이언트가 아무런 RPC를 생성하지 않고 있을 때, 서버가 클라이언트와의 연결을 끊는 시간을 설정합니다.
         * 여기서는 25초로 설정되어 있습니다.
         */
        ServerBuilder<?> builder = ServerBuilder.forPort(port).keepAliveTime(10, TimeUnit.SECONDS)
                .keepAliveTimeout(1,TimeUnit.SECONDS)
                .maxConnectionIdle(25,TimeUnit.SECONDS);

        Arrays.asList(services).forEach(builder::addService);
        return new GrpcServerSec11(builder.build());
    }

    public GrpcServerSec11 start(){
        List<String> services = server.getServices()
                .stream()
                .map(ServerServiceDefinition::getServiceDescriptor)
                .map(ServiceDescriptor::getName)
                .toList();
        try {
            server.start();
            log.info("server started. listening on port {}. services: {}", server.getPort(), services);
            return this;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void await(){
        try{
            server.awaitTermination();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        server.shutdownNow();

        log.info("server stopped");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6565)
                .addService(new BankService())
                .build();

        server.start();

        server.awaitTermination();

    }
}
