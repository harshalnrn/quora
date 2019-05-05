package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

  @Autowired QuestionBusinessService questionBusinessService;

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/create",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(
      QuestionRequest questionRequest, @RequestHeader("authorisationHeader") String accessToken)
      throws AuthorizationFailedException {
    QuestionsEntity questionsEntity = new QuestionsEntity();
    questionsEntity.setContent(questionRequest.getContent());
    questionsEntity.setDate(ZonedDateTime.now());
    // questionsEntity.setUserEntity();  //set in service by getting user from authTokenEntity
    questionsEntity.setUuid("1213");
    // note : token is like authorised user session, where its logged in users safe identity
    questionBusinessService.createQuestionService(questionsEntity, accessToken);

    QuestionResponse reponse = new QuestionResponse();
    reponse.setId(questionsEntity.getUuid());
    reponse.setStatus("QUESTION CREATED");
    ResponseEntity<QuestionResponse> responseEntity = new ResponseEntity(reponse, HttpStatus.OK);
    return responseEntity;
  }

  @RequestMapping(
      method = RequestMethod.GET,
      value = "/all",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
      @RequestHeader("authorisationHeader") String accessToken)
      throws AuthorizationFailedException {
    QuestionDetailsResponse response = new QuestionDetailsResponse();
    List<QuestionDetailsResponse> list = new LinkedList<QuestionDetailsResponse>();
    for (QuestionsEntity questionsEntity : questionBusinessService.getQuestionList(accessToken)) {
      response.setId(questionsEntity.getUuid());
      response.setContent(questionsEntity.getContent());
      list.add(response);
    }
    return new ResponseEntity<List<QuestionDetailsResponse>>(list, HttpStatus.OK);
  }
}
