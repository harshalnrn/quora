package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
  public void createQuestion (
      QuestionRequest questionRequest, @RequestHeader("authorisationHeader") String accessToken) throws AuthorizationFailedException
  {
      QuestionsEntity questionsEntity=new QuestionsEntity();
      questionsEntity.setContent(questionRequest.getContent());
      questionsEntity.setDate(ZonedDateTime.now());
     // questionsEntity.setUserEntity();  //set in service by getting user from authTokenEntity
      questionsEntity.setUuid("1213");
//note : token is like logged in users safe identity
      questionBusinessService.createQuestionService(questionsEntity,accessToken);

  }
}
