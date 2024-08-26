package org.example.common;



import org.example.sec10.BankService;


public class Demo {
    public static void main(String[] args) {

        GrpcServer.create(new BankService())
                .start()
                .await();

    }
}
