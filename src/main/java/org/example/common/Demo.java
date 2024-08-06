package org.example.common;

import org.example.sec06.BankService;
import org.example.sec06.TransferService;

public class Demo {
    public static void main(String[] args) {

        GrpcServer.create(new BankService(), new TransferService())
                .start()
                .await();

    }
}
