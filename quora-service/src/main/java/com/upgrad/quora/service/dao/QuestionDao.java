package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;


@Repository
public class QuestionDao {

  @Autowired
  private UserDao userDao;

  @PersistenceContext private EntityManager entityManager;

  public void createQuestion(QuestionsEntity questionsEntity) {
    entityManager.persist(questionsEntity);
  }

  public List<QuestionsEntity> getAllQuestions(String accessToken){
      List<QuestionsEntity> questionList = null;

      TypedQuery<QuestionsEntity> query = entityManager.createNamedQuery("allQuestions", QuestionsEntity.class);
      questionList = query.getResultList();

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
