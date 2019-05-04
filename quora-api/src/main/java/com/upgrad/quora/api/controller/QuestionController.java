package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

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

    //This controller method is called when the request pattern is of type '/delete{questionId}' and also the incoming request is of DELETE type
    //This method calls the deleteQuestionByUuid in the service layer by passing the questionUuid of the question to be deleted and accessToken of the logged in user
    //On success, returns Http Status Code 200 with message QUESTION DELETED
    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/delete/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<QuestionDeleteResponse> deleteQuestionByUuid(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionsEntity deletedQuestion = questionBusinessService.deleteQuestionByUuid(questionUuid, authorization);

        QuestionDeleteResponse response = new QuestionDeleteResponse().id(deletedQuestion.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>( response , HttpStatus.OK);
    }
}
