package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/create",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion (
      QuestionRequest questionRequest, @RequestHeader("authorisationHeader") String accessToken) throws AuthorizationFailedException
  {
      QuestionsEntity questionsEntity=new QuestionsEntity();
      questionsEntity.setContent(questionRequest.getContent());
      questionsEntity.setDate(ZonedDateTime.now());
     // questionsEntity.setUserEntity();  //set in service by getting user from authTokenEntity
      questionsEntity.setUuid("1213");
//note : token is like authorised user session, where its logged in users safe identity
      questionBusinessService.createQuestionService(questionsEntity,accessToken);

      QuestionResponse reponse=new QuestionResponse();
      reponse.setId(questionsEntity.getUuid());
      reponse.setStatus("QUESTION CREATED");
      ResponseEntity<QuestionResponse> responseEntity=new ResponseEntity(reponse, HttpStatus.OK);
      return responseEntity;
  }

    //This controller method is called when the request pattern is of type '/question/delete{questionId}' and also the incoming request is of DELETE type
    //This method calls the deleteQuestionByUuid in the service layer by passing the questionUuid of the question to be deleted and accessToken of the logged in user
    //On success, returns Http Status Code 200 with message QUESTION DELETED and uuid of deleted question
    //throws AuthorizationFailedException for the following conditions
    // - The given accessToken does not exist
    // - The user for given accessToken has signed out
    // - The user for the given accessToken is not the owner of the question to be deleted and is a non-admin user
    //throws InvalidQuestionException if the question with the given Uuid does not exist in the database
    //
    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/delete/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<QuestionDeleteResponse> deleteQuestionByUuid(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionsEntity deletedQuestion = questionBusinessService.deleteQuestionByUuid(questionUuid, accessToken);

        QuestionDeleteResponse response = new QuestionDeleteResponse().id(deletedQuestion.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>( response , HttpStatus.OK);
    }

    //This controller method is called when the request pattern is of type /question/all/{userId} and request is of GET type
    //This method calls the getQuestionsForUserId in the service layer by passing the userUuid of user whose questions should be fetched and accessToken of the logged in user
    //On success, returns Http Status Code 200 along with the question Uuid and content of all the fetched questions
    //throws AuthorizationFailedException for the following conditions
    // - The given accessToken does not exist
    // - The user for given accessToken has signed out
    //throws UserNotFoundException if the user with the given userUuid does not exist in the database
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<QuestionResponse>> getQuestionsByUserId(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

      List<QuestionsEntity> questions = questionBusinessService.getQuestionsForUserId(userId, accessToken);

      List<QuestionResponse> questionsResponse = new ArrayList<>();

      for(QuestionsEntity question : questions){
          QuestionResponse qResponse = new QuestionResponse().id(question.getUuid()).status(question.getContent());
          questionsResponse.add(qResponse);
      }

      return new ResponseEntity<List<QuestionResponse>>( questionsResponse, HttpStatus.OK);
    }
}