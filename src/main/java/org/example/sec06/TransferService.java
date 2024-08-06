package org.example.sec06;

import com.youyk.models.sec06.TransferRequest;
import com.youyk.models.sec06.TransferResponse;
import com.youyk.models.sec06.TransferServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.example.sec06.requesthandlers.TransferRequestHandler;

public class TransferService  extends TransferServiceGrpc.TransferServiceImplBase{
    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        return new TransferRequestHandler(responseObserver);
    }
}
