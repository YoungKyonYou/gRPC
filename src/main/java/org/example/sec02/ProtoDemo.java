package org.example.sec02;

import com.youyk.models.sec01.PersonOuterClass;
import com.youyk.models.sec02.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoDemo {
    private static final Logger log = LoggerFactory.getLogger(ProtoDemo.class);

    public static void main(String[] args){
        // create person1
        var person1 = createPerson();

        // create another instance with same values
        var person2 = createPerson();

        // compare
        log.info("equals {}", person1.equals(person2)); // true 객체가 다르나 같은 값이면 동일하다고 반환
        log.info("== {}", (person1 == person2)); // false 객체가 다름
        log.info("person1 : {}", person1);
        log.info("person2 : {}", person2);

        // mutable? No

        // create another instance with diff values
        var person3 = person1.toBuilder().setName("mike").build();
        log.info("person3: {}", person3); //person3 : name "mike"

        // compare
        log.info("equals {}", person1.equals(person3));// equals false
        log.info("== {}", (person1 == person3));// false

        //null? null을 set할 수 없음 그래서 clearName()를 대신해서 쓴다
        var person4 = person1.toBuilder().clearName().build();
        log.info("person4: {}", person4);//person4: age 12

    }

    private static Person createPerson(){
        return Person.newBuilder()
                .setName("sam")
                .setAge(12)
                .build();
    }
}
