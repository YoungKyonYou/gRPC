package org.example.sec10.validator;

import com.youyk.models.sec10.ErrorMessage;
import com.youyk.models.sec10.ValidationCode;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;

import java.util.Map;
import java.util.Optional;

public class RequestValidator {

    private static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

    public static Optional<StatusRuntimeException> validateAccount(int accountNumber){
        if(accountNumber > 0 && accountNumber < 11){
            return Optional.empty();
        }

        Metadata metadata = toMetadata(ValidationCode.INVALID_ACCOUNT);
        return Optional.of(Status.INVALID_ARGUMENT.asRuntimeException(metadata));
    }

    public static Optional<StatusRuntimeException> isAmountDivisibleBy10(int amount){
        if(amount > 0 && amount % 10 == 0){
            return Optional.empty();
        }
        Metadata metadata = toMetadata(ValidationCode.INVALID_AMOUNT);
        return Optional.of(Status.INVALID_ARGUMENT.asRuntimeException(metadata));
    }

    public static Optional<StatusRuntimeException> hasSufficientBalance(int amount, int balance){
        if(amount <= balance){
            return Optional.empty();
        }
        Metadata metadata = toMetadata(ValidationCode.INSUFFICIENT_BALANCE);
        return Optional.of(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
    }

    private static Metadata toMetadata(ValidationCode code){
        Metadata metadata = new Metadata();

        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setValidationCode(code)
                .build();
        metadata.put(ERROR_MESSAGE_KEY, errorMessage);
        Key<String> key = Key.of("desc", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(key, code.toString());
        return metadata;
    }

}
