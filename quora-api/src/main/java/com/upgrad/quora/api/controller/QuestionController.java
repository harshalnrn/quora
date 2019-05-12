package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This is a controller class for handling http requests related to Question Management
 * functionalities of quora application
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

  @Autowired QuestionBusinessService questionBusinessService;

  /**
   * This method used to handle http request of user to create/post a new question
   *
   * @param questionRequest
   * @param accessToken
   * @return returns ResponseEntity enbedded with model object or error object
   * @throws AuthorizationFailedException
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/create",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(
      QuestionRequest questionRequest, @RequestHeader("authorization") String accessToken)
      throws AuthorizationFailedException {
    QuestionsEntity questionsEntity = new QuestionsEntity();
    questionsEntity.setContent(questionRequest.getContent());
    questionsEntity.setDate(ZonedDateTime.now());
    questionsEntity.setUuid(UUID.randomUUID().toString());
    questionBusinessService.createQuestionService(questionsEntity, accessToken);
    QuestionResponse response = new QuestionResponse();
    response.setId(questionsEntity.getUuid());
    response.setStatus("QUESTION CREATED");
    ResponseEntity<QuestionResponse> responseEntity = new ResponseEntity(response, HttpStatus.OK);
    return responseEntity;
  }

  /**
   * This method is used to handle http request by user to get all the available questions on quora
   * application
   *
   * @param accessToken
   * @return returns ResponseEntity enbedded with model object or error object
   * @throws AuthorizationFailedException
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/all",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
      @RequestHeader("authorization") String accessToken) throws AuthorizationFailedException {
    QuestionDetailsResponse response = new QuestionDetailsResponse();
    List<QuestionDetailsResponse> list = new LinkedList<QuestionDetailsResponse>();
    for (QuestionsEntity questionsEntity : questionBusinessService.getQuestionList(accessToken)) {
      response.setId(questionsEntity.getUuid());
      response.setContent(questionsEntity.getContent());
      list.add(response);
    }
    return new ResponseEntity<List<QuestionDetailsResponse>>(list, HttpStatus.OK);
  }

  /**
   * This method used to handle http request by the owner /admin to edit a posted question
   *
   * @param quesUuid
   * @param accessToken
   * @param questionRequest
   * @return returns ResponseEntity enbedded with model object or error object
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(
      method = RequestMethod.PUT,
      value = "/edit/{questionId}",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestion(
      @PathVariable("questionId") String quesUuid,
      @RequestHeader("authorization") String accessToken,
      QuestionRequest questionRequest)
      throws AuthorizationFailedException, InvalidQuestionException {

    QuestionsEntity questionEntity = new QuestionsEntity();
    questionEntity.setUuid(quesUuid);
    questionEntity.setContent(questionRequest.getContent());

    questionBusinessService.editQuestionService(accessToken, questionEntity);

    QuestionEditResponse questionEditResponse =
        new QuestionEditResponse().id(quesUuid).status("QUESTION EDITED");
    return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.CREATED);
  }

  /**
   * This method is used to handle http request by owner/admin to delete the posted question
   *
   * @param questionUuid
   * @param accessToken
   * @return returns ResponseEntity enbedded with model object or error object
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(
      method = RequestMethod.DELETE,
      path = "/delete/{questionId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionDeleteResponse> deleteQuestionByUuid(
      @PathVariable("questionId") final String questionUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    questionBusinessService.deleteQuestionByUuid(questionUuid, accessToken);

    QuestionDeleteResponse response =
        new QuestionDeleteResponse().id(questionUuid).status("QUESTION DELETED");

    return new ResponseEntity<QuestionDeleteResponse>(response, HttpStatus.OK);
  }

  /**
   * This method is used to handle http request by user, to only retreive questions, that belong to
   * them
   *
   * @param userId
   * @param accessToken
   * @return returns ResponseEntity enbedded with model object or error object
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   */
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/all/{userId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionResponse>> getQuestionsByUserId(
      @PathVariable("userId") final String userId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, UserNotFoundException {

    List<QuestionsEntity> questions =
        questionBusinessService.getQuestionsForUserId(userId, accessToken);
    List<QuestionResponse> questionsResponse = new ArrayList<>();
    for (QuestionsEntity question : questions) {
      QuestionResponse qResponse =
          new QuestionResponse().id(question.getUuid()).status(question.getContent());
      questionsResponse.add(qResponse);
    }
    return new ResponseEntity<List<QuestionResponse>>(questionsResponse, HttpStatus.OK);
  }
}
