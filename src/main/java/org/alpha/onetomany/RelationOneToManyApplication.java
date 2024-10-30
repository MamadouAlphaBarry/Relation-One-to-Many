package org.alpha.onetomany;

import org.alpha.onetomany.entities.Author;
import org.alpha.onetomany.entities.Book;
import org.alpha.onetomany.repository.AuthorRepository;
import org.alpha.onetomany.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RelationOneToManyApplication implements CommandLineRunner {
    @Autowired
 private AuthorRepository authorRepository;
    @Autowired
 private BookRepository bookRepository;
    public static void main(String[] args) {
        SpringApplication.run(RelationOneToManyApplication.class, args);



    }

    @Override
    public void run(String... args) throws Exception {

        Author author = new Author();
        author.setName("George Orwell");
        authorRepository.save(author);
        Author author2 = authorRepository.getReferenceById(author.getId());
        Book book1 = new Book();
        book1.setTitle("1984");
        book1.setAuthor(author2);
        Book book2 = new Book();
        book2.setTitle("Animal Farm");
        book2.setAuthor(author2);
        bookRepository.save(book1);
        bookRepository.save(book2);

    }
}
