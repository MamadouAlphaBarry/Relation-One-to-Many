package org.alpha.onetomany.repository;

import org.alpha.onetomany.entities.Author;
import org.alpha.onetomany.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    /**Pour récupérer un éditeur et charger tous ses auteurs, livres, et chapitres associés en une seule requête,
     *  on peut utiliser un JOIN FETCH avec les collections imbriquées :*/

    @Query("SELECT p FROM Publisher p " +
            "JOIN FETCH p.authors a " +
            "JOIN FETCH a.books b " +
            "JOIN FETCH b.chapters c " +
            "WHERE p.id = :publisherId")
    Publisher findPublisherWithAuthorsBooksAndChapters(@Param("publisherId") Long publisherId);

}

