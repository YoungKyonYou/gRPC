package org.example.sec03;


import com.youyk.models.sec03.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Lec02Serialization {
    private static final Logger log = LoggerFactory.getLogger(Lec02Serialization.class);
    private static final Path PATH = Path.of("person.out");

    public static void main(String[] args) throws IOException {
        Person person = Person.newBuilder()
                .setLastName("sam")
                .setAge(12)
                .setEmail("sam@gmail.com")
                .setEmployed(true)
                .setSalary(1000.2345)
                .setBankAccountNumber(123456789012L)
                .setBalance(-10000)
                .build();


        serialize(person);
        log.info("{}", deserialize());
        log.info("equals: {}", person.equals(deserialize()));
        log.info("bytes length: {}", person.toByteArray());
    }

    public static void serialize(Person person) throws IOException {
        //try with resources 사용할 것! 그래서 자동으로 close하게끔 만들어야 함 - outputStream  쓸때
        try(var stream = Files.newOutputStream(PATH)) {
            person.writeTo(stream);
        }
        //person.writeTo(Files.newOutputStream(PATH));
    }

    public static Person deserialize() throws IOException {
        try(var stream = Files.newInputStream(PATH)){
            return Person.parseFrom(stream);
        }
        //return Person.parseFrom(Files.newInputStream(PATH));
    }
}
