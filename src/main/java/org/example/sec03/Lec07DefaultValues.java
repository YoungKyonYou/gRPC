package org.example.sec03;

import com.youyk.models.sec03.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Lec07DefaultValues {
    private static final Logger log = LoggerFactory.getLogger(Lec07DefaultValues.class);

    public static void main(String[] args) throws IOException {
        School school = School.newBuilder().build();

        log.info("{}", school.getId()); // 0이 출력됨
        log.info("{}", school.getName()); //아무것도 출력되지 않는다 NULL 도 아님
        log.info("{}", school.getAddress().getCity()); //아무것도 출력되지 않는다 NULL 도 아님
        //proto는 null이 없고 null을 Set 할 수도 있다 그렇기 때문에 clear를 써야 한다. 이렇게 함으로서 안전하다.

        log.info("is default : {}", school.getAddress().equals(Address.getDefaultInstance())); //true 반환

        //그렇다면 default 객체인지 아닌지 어떻게 알까?
        //has를 사용한다
        log.info("has address? {}", school.hasAddress()); //false 반환

        //collection
        Library lib = Library.newBuilder().build();
        log.info("{}",lib.getBooksList());//빈 리스트 []

        //map
        Dealer dealer = Dealer.newBuilder().build();
        log.info("{}", dealer.getInventoryMap());//빈 Map 출력 {}

        // enum
        var car = Car.newBuilder().build();
        log.info("{}", car.getBodyStyle()); //SEDAN 출력



    }


}
