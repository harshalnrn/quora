package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class QuestionBusinessService {

  @Autowired QuestionDao questionDao;

  public void createQuestionService(QuestionsEntity questionsEntity, String accessToken)
      throws AuthorizationFailedException {

    // check for business rules
    // Here do i need to check just if token exits, or check if this user has the passed, access
    // token (i.e loged in atleast once).Well there is no way, that a user can get any other access
    // token, since they get it only when they login

    UserAuthTokenEntity userAuthTokenEntity = questionDao.ValidateAccessToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // check if logged in user has signed out
    if ((userAuthTokenEntity.getLogoutAt()!=null)) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post a question");
    }

    // persist question after 2 checks
    questionsEntity.setUserEntity(userAuthTokenEntity.getUsers());
    questionDao.createQuestion(questionsEntity);
  }


  public QuestionsEntity getQuestion(String quesUuid) throws InvalidQuestionException
  {
    QuestionsEntity questionsEntity = questionDao.getQuestionByUuid(quesUuid);
    if (questionsEntity==null) {
      throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
    }
    return  questionsEntity;
  }

  public void editQuestionService(String quesUuid,String accessToken, QuestionsEntity questionsEntity) throws AuthorizationFailedException {
    UserAuthTokenEntity userAuthTokenEntity = questionDao.ValidateAccessToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if ((userAuthTokenEntity.getLogoutAt() != null) && userAuthTokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
    } else if (questionsEntity.getUserEntity().getId() != userAuthTokenEntity.getUsers().getId()) {
      throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
    } else {
      questionDao.editQuestion(questionsEntity);

    }
  }
}
