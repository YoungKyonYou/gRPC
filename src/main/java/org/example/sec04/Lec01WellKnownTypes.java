package org.example.sec04;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Timestamp;
import com.youyk.models.sec04.Person;
import com.youyk.models.sec04.Sample;
import com.youyk.models.sec04.common.Address;
import com.youyk.models.sec04.common.BodyStyle;
import com.youyk.models.sec04.common.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class Lec01WellKnownTypes {
    private static final Logger log = LoggerFactory.getLogger(Lec01WellKnownTypes.class);

    public static void main(String[] args){
        Sample sample = Sample.newBuilder()
                .setAge(Int32Value.of(12))
                .setLoginTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .build();

        log.info("{}", Instant.ofEpochSecond(sample.getLoginTime().getSeconds()));
    }
}
