package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import lombok.Data;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Data
@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //This method receives the Answer object to be persisted in the database
    public void createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
    }
}
