package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/** This service class manages all functionalities and business rules of answer management */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AnswerBusinessService {

  @Autowired private UserDao userDao;

  @Autowired private QuestionDao questionDao;

  @Autowired private AnswerDao answerDao;

  /**
   * This method manages business rules for posting new answer
   *
   * @param accessToken
   * @param questionUuid
   * @param answerEntity
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  public void createAnswer(String accessToken, String questionUuid, AnswerEntity answerEntity)
      throws AuthorizationFailedException, InvalidQuestionException {

    QuestionsEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
    }

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (hasUserSignedOut(userAuthTokenEntity.getLogoutAt())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post an answer");
    }

    // Populate the answerEntity with userEntity and questionEntity objects
    answerEntity.setUsers(userAuthTokenEntity.getUsers());
    answerEntity.setQuestionsEntity(questionEntity);

    answerDao.createAnswer(answerEntity);
  }

  /**
   * This method manages business rules to update an existing posted answer
   *
   * @param accessToken
   * @param answerUuid
   * @param updatedAnswer
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  public void updateAnswer(String accessToken, String answerUuid, String updatedAnswer)
      throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (hasUserSignedOut(userAuthTokenEntity.getLogoutAt())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to edit an answer");
    }

    AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerUuid);

    if (existingAnswer == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }

    UserEntity loggedUser = userAuthTokenEntity.getUsers();
    UserEntity answerOwner = existingAnswer.getUsers();

    // Checks if logged in user is owner of the answer
    if (!answerOwner.getUuid().equals(loggedUser.getUuid())) {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the answer owner can edit the answer");
    }

    existingAnswer.setAns(updatedAnswer);

    answerDao.updateAnswer(existingAnswer);
  }

  /**
   * This method manages business rules for deleting an existing posted answer. Performs various
   * authorization checks for the logged-in user who is trying to delete an answer. This method gets
   * the answer from the DB using UUID and then passes the Answer Entity to the DAO layer to remove
   * it
   *
   * @param answerUuid
   * @param accessToken
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  public void deleteAnswer(String answerUuid, String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {
    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (hasUserSignedOut(userAuthTokenEntity.getLogoutAt())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to edit an answer");
    }
    AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerUuid);
    if (answerEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    if (!answerEntity.getUsers().getUuid().equals(userAuthTokenEntity.getUsers().getUuid())
        && !userAuthTokenEntity.getUsers().getRole().equals("admin")) {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the answer owner or admin can delete the answer");
    }
    answerDao.deleteAnswer(answerEntity);
  }

  /**
   * This method checks if user has signed out or not
   *
   * @param loggedOutTime
   * @return
   */
  public boolean hasUserSignedOut(ZonedDateTime loggedOutTime) {
    return (loggedOutTime != null && ZonedDateTime.now().isAfter(loggedOutTime));
  }

  /**
   * This method manages business rules for retreiving list of answers of particular question
   *
   * @param questionUuid
   * @param token
   * @return List of Answers belonging to the particular question
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  public List<AnswerEntity> getAllAnswersOfQuestion(String questionUuid, String token)
      throws AuthorizationFailedException, InvalidQuestionException {

    UserAuthTokenEntity tokenEntity = userDao.getAuthToken(token);

    if (tokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (hasUserSignedOut(tokenEntity.getLogoutAt())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to get the answers");
    }

    QuestionsEntity questionsEntity = questionDao.getQuestionByUuid(questionUuid);
    if (questionsEntity == null) {
      throw new InvalidQuestionException(
          "QUES-001", "The question with entered uuid whose details are to be seen does not exist");
    }
    return answerDao.getAnswerByQUuid(questionsEntity);
  }
}
