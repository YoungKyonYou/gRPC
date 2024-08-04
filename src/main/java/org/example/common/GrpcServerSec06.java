package org.example.common;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.sec06.BankService;

import java.io.IOException;

public class GrpcServerSec06 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6565)
                .addService(new BankService())
                .build();

        server.start();

        server.awaitTermination();

    }
}
