package org.alpha.onetomany.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.alpha.onetomany.entities.Book;
import org.alpha.onetomany.repository.AuthorRepository;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookAuditService {
    @PersistenceContext
    private EntityManager entityManager;


    public List<?> getRevision(Long bookId){
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return  auditReader.createQuery().forRevisionsOfEntity(
                Book.class,false,true
        ).add(AuditEntity.id().eq(bookId)).getResultList();
    }
}

