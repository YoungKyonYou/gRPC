package org.example.common.sec13;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.example.sec06.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcServer {

    private final Server server;

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    public GrpcServer(Server server) {
        this.server = server;
    }

    public static org.example.common.GrpcServer create(BindableService... services){
        return create(6565, services);
    }
    public static org.example.common.GrpcServer create(int port, BindableService... services){
        ServerBuilder<?> builder = ServerBuilder.forPort(port);

        Arrays.asList(services).forEach(builder::addService);
        return new org.example.common.GrpcServer(builder.build());
    }

    /**
     * Consumer는 Java의 함수형 인터페이스 중 하나로,
     * 입력 값을 받아서 처리하는 작업을 수행하지만 결과를 반환하지 않는 함수형 인터페이스입니다.
     * 간단히 말해, 어떤 작업을 수행하기 위해 입력 값만 필요한 경우에 사용됩니다.
     * create를 ctrl+클릭해서 들어가보면 NettyServerBuilder를 받는다.
     */
    public static GrpcServer create(int port, Consumer<NettyServerBuilder> consumer){
        ServerBuilder<?> builder = ServerBuilder.forPort(port);

        consumer.accept((NettyServerBuilder)builder);

        return new GrpcServer(builder.build());
    }

    public GrpcServer start(){
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
