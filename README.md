# Relation-One-to-Many

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