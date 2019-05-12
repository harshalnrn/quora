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

/**
 * This Repository class has methods related to CRUD operation belonging to create/edit/delete/get
 * questions functionality
 */
@Repository
public class QuestionDao {

  @Autowired private UserDao userDao;

  @PersistenceContext private EntityManager entityManager;

  /**
   * This method is used to persist newly created question
   *
   * @param questionsEntity
   */
  public void createQuestion(QuestionsEntity questionsEntity) {
    entityManager.persist(questionsEntity);
  }

  /**
   * This method is used to retreive all the questions in quora application from the database
   *
   * @param accessToken
   * @return
   * @throws AuthorizationFailedException
   */
  public List<QuestionsEntity> getAllQuestions(String accessToken)
      throws AuthorizationFailedException {
    UserAuthTokenEntity tokenEntity = userDao.getAuthToken(accessToken);
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

  /**
   * This method is used to delete a question from the database
   *
   * @param questionEntity
   */
  public void deleteQuestionByUuid(QuestionsEntity questionEntity) {
    entityManager.remove(questionEntity);
  }

  /**
   * This method is used to get all the qestions belonging to a particular user, form the database
   *
   * @param userUuid
   * @return
   */
  public List<QuestionsEntity> getQuestionsForUserId(String userUuid) {
    try {
      TypedQuery<QuestionsEntity> query =
          entityManager.createNamedQuery("findQuestionsByUserId", QuestionsEntity.class);
      query.setParameter("userUuid", userUuid);
      return query.getResultList();
    } catch (NoResultException nrex) {
      return null;
    }
  }

  /**
   * This method is used to get a specific question based upon its Uuid
   *
   * @param quesUuid
   * @return
   */
  public QuestionsEntity getQuestionByUuid(String quesUuid) {
    try {
      return entityManager
          .createNamedQuery("QuestionByUuid", QuestionsEntity.class)
          .setParameter("uuid", quesUuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method is used to update an existing question
   *
   * @param questionsEntity
   */
  public void editQuestion(QuestionsEntity questionsEntity) {
    entityManager.merge(
        questionsEntity); // Changing the state of the entity from detached to persistent
  }
}
