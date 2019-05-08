package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionsEntity;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Data
@Repository
public class AnswerDao {


    @PersistenceContext
    private EntityManager entityManager;

    //This method receives the Answer object to be persisted in the database
    public void createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
    }

    //This method fetches the answerEntity for the given Uuid from the database
    //Returns AnswerEntity object for the given Uuid if it exists, else returns null
    public AnswerEntity getAnswerByUuid(String answerUuid){
        try {
            TypedQuery<AnswerEntity> answerByUuidQuery = entityManager.createNamedQuery("findAnswerByUuid", AnswerEntity.class);
            answerByUuidQuery.setParameter("uuid", answerUuid);
            return answerByUuidQuery.getSingleResult();
        } catch (NoResultException nrex) {
            return null;
        }
    }

    //This method receives the AnswerEntity to be updated in the database
    public void updateAnswer(AnswerEntity updatedAnswerEntity){
        entityManager.merge(updatedAnswerEntity);
    }

    public void deleteAnswer(AnswerEntity answerEntity) { entityManager.remove(answerEntity);}


}
