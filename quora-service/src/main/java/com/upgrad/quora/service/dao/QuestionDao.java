package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import lombok.Data;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Data
@Repository
public class QuestionDao {

  @PersistenceContext private EntityManager entityManager;

  public void createQuestion(QuestionsEntity questionsEntity) {
    entityManager.persist(
        questionsEntity); // where to handled SQL exception during this operation? Transaction block
                          // ? ex: constraint violation
  }

  public UserAuthTokenEntity ValidateAccessToken(String accessToken) {

    try {
      TypedQuery<UserAuthTokenEntity> query =
          entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class);
      query.setParameter("access_token", accessToken);
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
}
