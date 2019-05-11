package com.upgrad.quora.service.business;

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


@Service
@Transactional(propagation = Propagation.REQUIRED)
public class QuestionBusinessService {

  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private UserDao userDao;

  public void createQuestionService(QuestionsEntity questionsEntity, String accessToken)
      throws AuthorizationFailedException {

    // check for business rules
    // Here do i need to check just if token exits, or check if this user has the passed, access
    // token (i.e loged in atleast once).Well there is no way, that a user can get any other access
    // token, since they get it only when they login

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // check if logged in user has signed out
    if ((userAuthTokenEntity.getLogoutAt() != null)) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post a question");
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
      Returns the questions Entity object back to the controller method
    */
  public QuestionsEntity editQuestionService(String quesUuid,String accessToken) throws InvalidQuestionException,AuthorizationFailedException
  {
    final QuestionsEntity questionsEntity = questionDao.getQuestionByUuid(quesUuid);
    final UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
    if (questionsEntity==null) {
      throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
    }
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if ((userAuthTokenEntity.getLogoutAt() != null) && userAuthTokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
    }
    // Checking if the Logged In User is owner of the question or not
    else if (!questionsEntity.getUserEntity().getId().equals(userAuthTokenEntity.getUsers().getId())) {
      throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
    }
    return  questionsEntity;
  }

  /*
  The following persists the edited question in the Database by passing the updated Question Entity to the DAO layer
   */
  public void updateQuestion(QuestionsEntity updatedQuestionsEntity) {
      questionDao.editQuestion(updatedQuestionsEntity);
    }


  public List<QuestionsEntity> getQuestionList(String accessToken)
      throws AuthorizationFailedException {
    return questionDao.getAllQuestions(accessToken);
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
      throw new AuthorizationFailedException("ATHR-001" , "User has not signed in");
    }

    //Check if logged in user has signed out
    if(hasUserSignedOut(userAuthTokenEntity.getLogoutAt())){
      throw new AuthorizationFailedException("ATHR-002" , "User is signed out.Sign in first to delete a question");
    }

    QuestionsEntity questionToDelete = questionDao.getQuestionByUuid(uuid);
    if(questionToDelete == null){
      throw new InvalidQuestionException("QUES-001" , "Entered question uuid does not exist");
    }
    UserEntity loggedInUser = userAuthTokenEntity.getUsers();
    UserEntity questionOwner = questionToDelete.getUserEntity();

    //Check the logged user is neither question owner nor admin user
    if(!questionOwner.getUuid().equals(loggedInUser.getUuid()) && !("admin").equals(loggedInUser.getRole())){
      throw new AuthorizationFailedException("ATHR-003" , "Only the question owner or admin can delete the question");
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
          throw new AuthorizationFailedException("ATHR-001" , "User has not signed in");
      }
      if(hasUserSignedOut(userAuthTokenEntity.getLogoutAt())){
          throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
      }
      UserEntity user = userDao.getUserbyUuid(userUuid);
      if(user == null){
          throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
      }
      return questionDao.getQuestionsForUserId(userUuid);
  }
}
