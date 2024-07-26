package org.example.sec04;

import com.youyk.models.sec04.Person;
import com.youyk.models.sec04.common.Address;
import com.youyk.models.sec04.common.BodyStyle;
import com.youyk.models.sec04.common.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01Import {
    private static final Logger log = LoggerFactory.getLogger(Lec01Import.class);

    public static void main(String[] args){
        Address address = Address.newBuilder().setCity("atlanta").build();
        Car car = Car.newBuilder().setBodyStyle(BodyStyle.COUPE).build();
        Person person = Person.newBuilder()
                .setName("sam")
                .setAge(12)
                .setCar(car)
                .setAddress(address)
                .build();


        log.info("{}", person);
    }
}
