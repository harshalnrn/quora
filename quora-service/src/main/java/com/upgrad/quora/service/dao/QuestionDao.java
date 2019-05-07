package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import lombok.Data;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Repository
public class QuestionDao {

  @PersistenceContext private EntityManager entityManager;

  public void createQuestion(QuestionsEntity questionsEntity) {
    entityManager.persist(questionsEntity);
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

  public List<QuestionsEntity> getAllQuestions(String accessToken)
      throws AuthorizationFailedException {
    UserAuthTokenEntity tokenEntity = ValidateAccessToken(accessToken);
    List<QuestionsEntity> questionList = null;
    if (tokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (tokenEntity.getLogoutAt() != null
        && tokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to get all questions");
    } else {
      TypedQuery<QuestionsEntity> query =
          entityManager.createNamedQuery("allQuestions", QuestionsEntity.class);
      questionList = query.getResultList();
    }
    return questionList;
  }


  //This method receives the QuestionEntity of the question to be deleted from the database and removes it
  public void deleteQuestionByUuid(QuestionsEntity questionEntity){
    entityManager.remove(questionEntity);
  }

  //This method executes Named query to fetch all the questions for the specified userUuid
  //Returns all the questions for the given userUuid found in the database
  //Returns null if there are no questions for the given userUuid - TODO - Check this
  public List<QuestionsEntity> getQuestionsForUserId(String userUuid){
      try {
          TypedQuery<QuestionsEntity> query = entityManager.createNamedQuery("findQuestionsByUserId", QuestionsEntity.class);
          query.setParameter("userUuid", userUuid);
          return query.getResultList();
      } catch (NoResultException nrex) {
          return null;
      }
  }

  public QuestionsEntity getQuestionByUuid(String quesUuid)
  {
    try {
      return entityManager.createNamedQuery("QuestionByUuid", QuestionsEntity.class).setParameter("uuid",quesUuid)
              .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /*
  This method
   */
  public void editQuestion(QuestionsEntity questionsEntity) {
    entityManager.merge(questionsEntity);   // Changing the state of the entity from detached to persistent
  }
}
