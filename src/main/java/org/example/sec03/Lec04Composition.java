package org.example.sec03;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youyk.models.sec03.Address;
import com.youyk.models.sec03.Person;
import com.youyk.models.sec03.School;
import com.youyk.models.sec03.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Lec04Composition {
    private static final Logger log = LoggerFactory.getLogger(Lec04Composition.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        // create student
        var address = Address.newBuilder()
                .setStreet("123 main st")
                .setCity("atlanta")
                .setState("GA")
                .build();
        var student = Student.newBuilder()
                .setName("sam")
                .setAddress(address)
                .build();
        // create school
        var school = School.newBuilder()
                .setId(1)
                .setName("high school")
                .setAddress(address.toBuilder().setStreet("234 main st").build())
                .build();

        log.info("school: {}", school);
        log.info("student: {}", student);

    }


}
