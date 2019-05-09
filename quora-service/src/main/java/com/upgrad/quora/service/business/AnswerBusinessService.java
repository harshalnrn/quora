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

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AnswerBusinessService {

  @Autowired private UserDao userDao;

  @Autowired private QuestionDao questionDao;

  @Autowired private AnswerDao answerDao;

  // This method calls the createAnswer() method in the AnswerRepository class to persist the answer
  // in the database
  // This method populates answerEntity with userEntity and questionEntity objects and sends the
  // answerEntity to DAO to be persisted in the database
  // throws AuthorizationFailedException for the following conditions
  // - The given accessToken does not exist
  // - The user for given accessToken has signed out
  // throws InvalidQuestionException if the question with the given Uuid does not exist in the
  // database
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

  // This method calls the updateAnswer() method in the AnswerRepository class to update the answer
  // in the database if the following conditions are true
  // - The given accessToken exists in the database
  // - The user performing the operation is logged in
  // - The user performing the edit is the owner of the answer
  // - The answer with the given uuid exists in the database
  // throws AuthorizationFailedException for the following conditions
  // - The given accessToken does not exist in the database
  // - The user for given accessToken has signed out
  // - The logged in user is not the owner of the answer begin edited
  // throws AnswerNotFoundException if the answer with the given Uuid does not exist in the database
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

  /*
  This method calls the delete answer method in the DAO class to delete the answer in the Database. It accepts the answer UUID
  and the access token of the Logged in user.
  throws AuthorizationFailedException if
  1. User has not signed in  (Access Token does not exist in the Database)
  2. User has logged out (Logout time is updated in the Database and current time is ahead of the logged out time)
  3. The user who posted the answer is not the same who is trying to delete the answer and also is non-admin

  throws AnswerNotFoundException if
  The answer UUID does not exist in the Database
  Gets the answer persisted in the DB by the UUID (returns null if not present) and passes it to the DAO to delete the same
   */
    public void deleteAnswer(String answerUuid, String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
        if (userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001" , "User has not signed in");
        }
        if (hasUserSignedOut(userAuthTokenEntity.getLogoutAt())) {
            throw new AuthorizationFailedException("ATHR-002" , "User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity answerEntity  = answerDao.getAnswerByUuid(answerUuid);
        if (answerEntity==null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        if (!answerEntity.getUsers().getUuid().equals(userAuthTokenEntity.getUsers().getUuid()) && !userAuthTokenEntity.getUsers().getRole().equals("admin")){
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }
        answerDao.deleteAnswer(answerEntity);
    }

    //Checks if the user has signed out by comparing if the current time is after the loggedOutTime received by the method
    //Returns true if the current time is after loggedOutTime(signout has happened), false otherwise
    public boolean hasUserSignedOut(ZonedDateTime loggedOutTime){
        return ( loggedOutTime != null && ZonedDateTime.now().isAfter(loggedOutTime) );
    }

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
