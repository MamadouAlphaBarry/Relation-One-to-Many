# Relation-One-to-Many

```java
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Book> books;

    // Getters et Setters
}
import javax.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    // Getters et Setters
}

```
```sql
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

```
La relation One-to-Many en JPA est définie dans l'entité principale (par exemple, Author) avec l’annotation @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY), où mappedBy indique que l’autre entité (par exemple, Book) possède la clé étrangère pour cette relation. Le cascade = CascadeType.ALL applique toutes les opérations (persist, merge, remove, etc.) de l'entité Author à ses Book, et fetch = FetchType.LAZY signifie que les livres sont chargés uniquement lorsqu'ils sont explicitement accédés.

Dans l'entité cible (par exemple, Book), on utilise @ManyToOne pour établir la connexion inverse. Cette relation est renforcée par @JoinColumn(name = "author_id"), qui crée la colonne author_id dans la table Book pour stocker la clé étrangère de l’auteur associé. Le choix entre Set, List, ou Map pour les collections dans @OneToMany dépend des besoins (unicité, ordre, ou association clé-valeur). Pour des chargements optimisés, on peut combiner cette relation avec des Entity Graphs ou JOIN FETCH pour contrôler
les données chargées en fonction des besoins, améliorant ainsi les performances.
# LES JOINTURES POSSIBLES
## JOIN (Inner Join)
Utilisé pour récupérer uniquement les entités où les deux côtés de la relation sont présents.
Exemple : Si on joint Author et Book, seuls les auteurs ayant au moins un livre seront récupérés.
```jpql
@Query("SELECT a FROM Author a JOIN a.books b WHERE b.title = :title")
List<Author> findAuthorsByBookTitle(@Param("title") String title);

```
## LEFT JOIN (Outer Join)
Charge toutes les entités du côté gauche (par exemple, Author) même si elles n’ont pas d’entités associées du côté droit (Book). Si un auteur n’a pas de livres, il sera toujours inclus dans le résultat.
Exemple : Permet de récupérer tous les auteurs, même ceux sans livres.
```jpql
@Query("SELECT a FROM Author a LEFT JOIN a.books b WHERE b.title = :title")
List<Author> findAuthorsByBookTitleIncludingNoBooks(@Param("title") String title);

```
## JOIN FETCH
Combine JOIN avec le chargement anticipé (FETCH), permettant de charger toutes les entités associées en une seule requête, même si la relation est définie avec FetchType.LAZY.
Utilisé pour éviter le problème de N+1 (où chaque entité est chargée avec une requête séparée), surtout dans des relations LAZY.
```jpql
@Query("SELECT a FROM Author a JOIN FETCH a.books WHERE a.id = :authorId")
Optional<Author> findAuthorWithBooks(@Param("authorId") Long authorId);

```
## JOIN FETCH
Combine JOIN avec le chargement anticipé (FETCH), permettant de charger toutes les entités associées en une seule requête, même si la relation est définie avec FetchType.LAZY.
Utilisé pour éviter le problème de N+1 (où chaque entité est chargée avec une requête séparée), surtout dans des relations LAZY.
```jql
@Query("SELECT a FROM Author a JOIN FETCH a.books WHERE a.id = :authorId")
Optional<Author> findAuthorWithBooks(@Param("authorId") Long authorId);

```
## LEFT JOIN FETCH
Similaire à JOIN FETCH mais inclut toutes les entités du côté gauche (par exemple, Author) même si elles n'ont pas d'entités associées du côté droit (Book).
Utile pour charger toutes les entités principales, même celles sans éléments associés, tout en optimisant la requête pour les relations LAZY.

```jql
@Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.name = :name")
List<Author> findAuthorsByNameWithBooks(@Param("name") String name);

```
## JOIN ON
   Permet d’ajouter des conditions directement dans la clause JOIN, limitant les entités associées qui sont récupérées dans la jointure.
   Moins fréquemment utilisé dans les relations @OneToMany, mais utile si l’on souhaite filtrer des associations basées sur certaines conditions.
```jpql
@Query("SELECT a FROM Author a LEFT JOIN a.books b ON b.publishedYear > :year WHERE a.name = :name")
List<Author> findAuthorsWithRecentBooks(@Param("year") int year, @Param("name") String name);

```

# Concept d'Agrégation
L'agrégation se réfère à la capacité de regrouper ou d'accumuler des valeurs sur des collections d'entités. Dans le contexte d'une relation One-to-Many, cela signifie qu'une entité principale peut avoir plusieurs entités associées, et l'on peut effectuer des opérations sur cette collection, telles que la somme, la moyenne, ou le comptage.
## COUNT
Utilisé pour compter le nombre d'entités associées. Cela peut être utile pour savoir combien de livres un auteur a écrit, par exemple.
```jpql
@Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
int countBooksByAuthor(@Param("authorId") Long authorId);

```
## SUM
Utilisé pour additionner une valeur numérique sur les entités associées. Par exemple, si chaque livre a un prix, on pourrait vouloir calculer le prix total des livres d'un auteur.
```jpql
@Query("SELECT SUM(b.price) FROM Book b WHERE b.author.id = :authorId")
Double sumBookPricesByAuthor(@Param("authorId") Long authorId);

```
## AVG
Utilisé pour calculer la moyenne d'une valeur numérique sur les entités associées. Par exemple, pour calculer le prix moyen des livres d'un auteur.

```jpql
@Query("SELECT AVG(b.price) FROM Book b WHERE b.author.id = :authorId")
Double averageBookPriceByAuthor(@Param("authorId") Long authorId);

```
## MAX et MIN
Utilisés pour obtenir la valeur maximale ou minimale d'un attribut sur les entités associées. Par exemple, on peut vouloir connaître le prix le plus élevé ou le plus bas parmi les livres d'un auteur.
```jpql
@Query("SELECT MAX(b.price) FROM Book b WHERE b.author.id = :authorId")
Double maxBookPriceByAuthor(@Param("authorId") Long authorId);

@Query("SELECT MIN(b.price) FROM Book b WHERE b.author.id = :authorId")
Double minBookPriceByAuthor(@Param("authorId") Long authorId);

```
## Utilisation des Agrégations dans les Requêtes
Les agrégations peuvent être utilisées avec des clauses GROUP BY pour obtenir des résultats agrégés par groupe. Par exemple, on peut vouloir savoir combien de livres chaque auteur a écrit, regroupés par auteur.
```jpql
@Query("SELECT a.name, COUNT(b) FROM Author a LEFT JOIN b.books b GROUP BY a.name")
List<Object[]> countBooksByAuthorGroupByName();

```
# Gestion des Audits
## Dependence 

## Etape 1: Activer l'audit dans la configuration
Dans une classe de configuration uitliser l'annotation @EnableJpaAuditing
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Configuration JPA additionnelle si nécessaire
}
```

## Etape 2: AjouterAuditingListner aux Entitès

Les entitès Auditè doivent etre annotèes avec @EntityListners(AuditingEntityListner.class) 

## Utilier les annotations d'audits
@CreatedDate: stocke la date et l'heure de createion de l'entitè
@LastModifiedDate: Stocke la date de la derniere modification
@CreatedBy:  Stock l'user qui a crèe l'entitè
@LastModifiedBy: Stock user qui a modifier l'entite
```java
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    // Getters, setters...
}

```
## Gestion des Champs d’Audit par l’Utilisateur
spring offre l'interface AuditorAware<T> pour les champs d'audits
Creer une class qui implemente l'interface et definire les valeurs des champs
en redifinissant les methodes

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}

import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Simulons un utilisateur connecté en utilisant Spring Security ou une autre méthode d'authentification
        // Retourne par exemple un nom d'utilisateur ou un ID d'utilisateur.
        return Optional.of("admin"); // Simulé, en pratique obtenir depuis le contexte de sécurité
    }
}

```
# Audit avancè

```properties
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-envers</artifactId>
    <version>${hibernate.version}</version>
</dependency>

```
Envers etant la fonctionnalitèes de Spring Data , est une extension de de Hibernate pour la gestion de l'historique.
Elle permet de:
1. Crèer automatiquement des revisions (ou versions) des entitès
2. Suivre les modifications de chaque attribut d'une entitè
3. stocker les metadonnèes sur chaque changement (comme la date et l'utilisateur)
## Configurer Envers et Activer l'Audit
Ajouter l'annotation @Audited sur les entitèes Auditer, cela indique a envers de creer une table d'audit pour chaque entitè

```java
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Audited
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    // Getters, setters...
}

```
## Tables d'Audits Crèe
Envers genere des tables d'audits avec les suffixe _AUD, qui contiennent:
* .Les données des champs de l’entité pour chaque révision.
* .Un identifiant de révision (numéro de révision) pour chaque changement.
* .Les métadonnées comme la date de révision et, si configuré, l’utilisateur ayant effectué la modification.

## Recuperation de l'historique avec envers
Hibernate Envers fournit une API pour accéder aux révisions d’une entité.
Cela permet de récupérer les versions précédentes ou d’inspecter les changements.
## Exemple de Service pour Lire l'Historique d'une Entité

Utilisez AuditReader pour interroger les versions d’une entité.
```java
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ProductAuditService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getRevisions(Long productId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Obtenir toutes les révisions de l'entité Product avec cet ID
        return auditReader.createQuery()
                .forRevisionsOfEntity(Product.class, false, true)
                .add(AuditEntity.id().eq(productId))
                .getResultList();
    }
}

```

# Exemples de Questions a Répondre par l'Audit

#### Quel était le titre original d'un livre ?
#### Qui était l'auteur initial d'un livre donné ?
#### Quelles modifications ont été faites et à quelles dates ?
