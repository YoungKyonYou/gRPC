package org.example.common.sec011.loadbalance;


import org.example.common.GrpcServer;
import org.example.sec06.BankService;

public class BankInstance1 {
    public static void main(String[] args){
        GrpcServer.create(6565, new BankService())
                .start()
                .await();
    }
}
