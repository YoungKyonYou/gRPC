package org.example.sec03;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.youyk.models.sec03.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Lec05Collection {
    private static final Logger log = LoggerFactory.getLogger(Lec05Collection.class);

    public static void main(String[] args) throws IOException {
        // create books
        Book book1 = Book.newBuilder()
                .setTitle("harry potter - part 1")
                .setAuthor("j k rowling")
                .setPublicationYear(1997)
                .build();
        Book book2 = book1.toBuilder().setTitle("harry potter - part 2").setPublicationYear(1998).build();
        Book book3 = book1.toBuilder().setTitle("harry potter - part 3").setPublicationYear(1999).build();

        Library library = Library.newBuilder()
                .setName("fantasy library")
//                .addBooks(book1)
//                .addBooks(book2)
//                .addBooks(book3)
                .addAllBooks(List.of(book1, book2, book3))
                .build();

        log.info("{}", library);


    }


}
