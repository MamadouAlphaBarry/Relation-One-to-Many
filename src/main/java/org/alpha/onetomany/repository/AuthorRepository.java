package org.alpha.onetomany.repository;

import org.alpha.onetomany.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
