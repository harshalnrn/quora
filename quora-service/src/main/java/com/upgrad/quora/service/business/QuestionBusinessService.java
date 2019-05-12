package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericExceptionCode;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import sun.net.www.content.text.Generic;


@Service
@Transactional(propagation = Propagation.REQUIRED)
public class QuestionBusinessService {

  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private UserDao userDao;

  public void createQuestionService(QuestionsEntity questionsEntity, String accessToken) throws AuthorizationFailedException {

    // check for business rules
    // Here do i need to check just if token exits, or check if this user has the passed, access
    // token (i.e loged in atleast once).Well there is no way, that a user can get any other access
    // token, since they get it only when they login

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_001.getCode() , GenericExceptionCode.ATHR_001.getDescription());
    }

    // check if logged in user has signed out
    if ((userAuthTokenEntity.getLogoutAt() != null)) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_002_QUES.getCode(),
                                             GenericExceptionCode.ATHR_002_QUES.getDescription());
    }

    // persist question after 2 checks
    questionsEntity.setUserEntity(userAuthTokenEntity.getUsers());
    questionDao.createQuestion(questionsEntity);
  }

    /*
      The following method performs valid Authorization checks before a user is allowed to edit the question as follows
      1. If the Entered question UUID does not exist
      2. If the Access Token does not exist in the Database
      3. If the User is Signed Out and he/she is trying to edit the question
      4. If the logged in user is the owner of the question, then only he can edit the question
      In case of failure of the conditions stated above, the method throws AuthorizationFailedException in case of 1,2 and 3
      and InvalidQuestionException in case of 4
    */
  public void editQuestionService(String accessToken, QuestionsEntity questionEntity) throws InvalidQuestionException,AuthorizationFailedException
  {
    final QuestionsEntity existingQuestionEntity = questionDao.getQuestionByUuid(questionEntity.getUuid());
    final UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);

    if (existingQuestionEntity==null) {
      throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
    }

    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_001.getCode() , GenericExceptionCode.ATHR_001.getDescription());
    } else if ((userAuthTokenEntity.getLogoutAt() != null) && userAuthTokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_002_QUES.getCode(), GenericExceptionCode.ATHR_002_QUES
          .getDescription());
    }
    // Checking if the Logged In User is owner of the question or not
    else if (!existingQuestionEntity.getUserEntity().getId().equals(userAuthTokenEntity.getUsers().getId())) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_003_QUES_EDIT.getCode(), GenericExceptionCode.ATHR_003_QUES_EDIT.getDescription());
    }

    existingQuestionEntity.setContent(questionEntity.getContent());

    questionDao.editQuestion(existingQuestionEntity);

  }

  /*
  The following persists the edited question in the Database by passing the updated Question Entity to the DAO layer
   */
  public void updateQuestion(QuestionsEntity updatedQuestionsEntity) {
      questionDao.editQuestion(updatedQuestionsEntity);
    }


  public List<QuestionsEntity> getQuestionList(String accessToken) throws AuthorizationFailedException {

    UserAuthTokenEntity tokenEntity = userDao.getAuthToken(accessToken);

    if (tokenEntity == null) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_001.getCode() , GenericExceptionCode.ATHR_001.getDescription());
    } else if (tokenEntity.getLogoutAt() != null
        && tokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_002_QUES_GET.getCode(), GenericExceptionCode.ATHR_002_QUES_GET.getDescription());
    }

    return questionDao.getAllQuestions();
  }

  //The method deletes the question for the given Uuid from the database if all of the following conditions are true
  // - The given accessToken exists in the database
  // - The user corresponding to the given accessToken is signed in
  // - The question for the given Uuid exists in the database
  // - The user deleting the question is either owner of the question with the given Uuid or admin user
  //throws AuthorizationFailedException for the following conditions
  // - The given accessToken does not exist
  // - The user for given accessToken has signed out
  // - The user for the given accessToken is not the owner of the question to be deleted and is a non-admin user
  //throws InvalidQuestionException if the question with the given Uuid does not exist in the database
  public void deleteQuestionByUuid(String uuid, String accessToken) throws AuthorizationFailedException, InvalidQuestionException{

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if(userAuthTokenEntity == null){
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_001.getCode() , GenericExceptionCode.ATHR_001.getDescription());
    }

    //Check if logged in user has signed out
    if(hasUserSignedOut(userAuthTokenEntity.getLogoutAt())){
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_002_QUES_DELETE.getCode(), GenericExceptionCode.ATHR_002_QUES_DELETE.getDescription());
    }

    QuestionsEntity questionToDelete = questionDao.getQuestionByUuid(uuid);
    if(questionToDelete == null){
      throw new InvalidQuestionException(GenericExceptionCode.QUES_001.getCode() , GenericExceptionCode.QUES_001.getDescription());
    }
    UserEntity loggedInUser = userAuthTokenEntity.getUsers();
    UserEntity questionOwner = questionToDelete.getUserEntity();

    //Check the logged user is neither question owner nor admin user
    if(!questionOwner.getUuid().equals(loggedInUser.getUuid()) && !("admin").equals(loggedInUser.getRole())){
      throw new AuthorizationFailedException(GenericExceptionCode.ATHR_003_QUES_DELETE.getCode() , GenericExceptionCode.ATHR_003_QUES_DELETE.getDescription());
    }
    questionDao.deleteQuestionByUuid(questionToDelete);
  }

  //Checks if the user has signed out by comparing if the current time is after the loggedOutTime received by the method
  //Returns true if the currenttime is after loggedOutTime(signout has happened), false otherwise
  public boolean hasUserSignedOut(ZonedDateTime loggedOutTime){
      return ( loggedOutTime != null && ZonedDateTime.now().isAfter(loggedOutTime) );
  }

    //The method retrieves all the questions from the database if all of the following conditions are true
    // - The given accessToken exists in the database
    // - The user corresponding to the given accessToken is signed in
    // - The user for the given userUuid exists in the database
    //Returns all the questions for the given userUuid
    //throws AuthorizationFailedException for the following conditions
    // - The given accessToken does not exist
    // - The user for given accessToken has signed out
    //throws UserNotFoundException if the user with the given userUuid does not exist in the database
  public List<QuestionsEntity> getQuestionsForUserId(String userUuid, String accessToken) throws AuthorizationFailedException, UserNotFoundException{

      UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
      if(userAuthTokenEntity == null){
          throw new AuthorizationFailedException(GenericExceptionCode.ATHR_001.getCode() , GenericExceptionCode.ATHR_001.getDescription());
      }
      if(hasUserSignedOut(userAuthTokenEntity.getLogoutAt())){
          throw new AuthorizationFailedException(GenericExceptionCode.ATHR_002_QUES_GET_USER.getCode(), GenericExceptionCode.ATHR_002_QUES_GET_USER.getDescription());
      }
      UserEntity user = userDao.getUserbyUuid(userUuid);
      if(user == null){
          throw new UserNotFoundException(GenericExceptionCode.USR_001_QUES_GET_USER.getCode(), GenericExceptionCode.USR_001_QUES_GET_USER.getDescription());
      }
      return questionDao.getQuestionsForUserId(userUuid);
  }
}
