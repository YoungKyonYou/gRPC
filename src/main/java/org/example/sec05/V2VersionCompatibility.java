package org.example.sec05;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youyk.models.sec05.v2.Television;
import com.youyk.models.sec05.v2.Type;
import org.example.sec05.parser.V1Parser;
import org.example.sec05.parser.V2Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V2VersionCompatibility {
    private static final Logger log = LoggerFactory.getLogger(V2VersionCompatibility.class);

    public static void main(String[] args) throws InvalidProtocolBufferException {
        Television tv = Television.newBuilder()
                .setBrand("samsung")
                .setModel(2019)
                .setType(Type.UHD)
                .build();

        V1Parser.parse(tv.toByteArray());
        V2Parser.parse(tv.toByteArray());
    }
}
