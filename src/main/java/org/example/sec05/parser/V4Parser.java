package org.example.sec05.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youyk.models.sec05.v4.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V4Parser {
    private static final Logger log = LoggerFactory.getLogger(V4Parser.class);

    public static void parse(byte[] bytes) throws InvalidProtocolBufferException {
        Television tv = Television.parseFrom(bytes);
        log.info("brand : {}", tv.getBrand());
        log.info("type: {}", tv.getType());
        log.info("price: {}", tv.getPrice());
    }
}
