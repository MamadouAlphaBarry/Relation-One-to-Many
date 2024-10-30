package org.alpha.onetomany.repository;

import org.alpha.onetomany.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
