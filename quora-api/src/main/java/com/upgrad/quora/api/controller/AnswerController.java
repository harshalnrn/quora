package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/** This controller class manages the answer management functionalities */
@RestController
public class AnswerController {

  @Autowired private AnswerBusinessService answerBusinessService;

  /**
   * This method handles the Http Request for posting an answer to a question
   *
   * @param answerRequest
   * @param questionUuid
   * @param accessToken
   * @return
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/question/{questionId}/answer/create",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(
      final AnswerRequest answerRequest,
      @PathVariable("questionId") final String questionUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setAns(answerRequest.getAnswer());
    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setDate(ZonedDateTime.now());

    answerBusinessService.createAnswer(accessToken, questionUuid, answerEntity);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>( answerResponse , HttpStatus.CREATED);
    }

  /**
   * This method handles the Http request for updating a post answer
   *
   * @param answerEditRequest
   * @param answerUuid
   * @param accessToken
   * @return
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  @RequestMapping(
      method = RequestMethod.PUT,
      path = "/answer/edit/{answerId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswer(
      final AnswerEditRequest answerEditRequest,
      @PathVariable("answerId") final String answerUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {
    answerBusinessService.updateAnswer(accessToken, answerUuid, answerEditRequest.getContent());

        AnswerEditResponse editResponse = new AnswerEditResponse().id(answerUuid).status("ANSWER EDITED");

    return new ResponseEntity<AnswerEditResponse>(editResponse, HttpStatus.OK);
  }

  /**
   * This method handles a HTTP request for deleting a posted answer
   *
   * @param answerUuid
   * @param accessToken
   * @return
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  @RequestMapping(
      method = RequestMethod.DELETE,
      path = "/answer/delete/{answerId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
      @PathVariable("answerId") final String answerUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {
    answerBusinessService.deleteAnswer(answerUuid, accessToken);
    AnswerDeleteResponse answerDeleteResponse =
        new AnswerDeleteResponse().id(answerUuid).status("ANSWER DELETED");
    return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
  }

  /**
   * This method handles HTTP request to retreive list of asnwers for a particular question
   *
   * @param questionId
   * @param accessToken
   * @return
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/answer/all/{questionId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersOfQuestion(
      @PathVariable("questionId") String questionId,
      @RequestHeader("authorization") String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    List<AnswerEntity> ansList =
        answerBusinessService.getAllAnswersOfQuestion(questionId, accessToken);

    List<AnswerDetailsResponse> list = new LinkedList<AnswerDetailsResponse>();
    for (AnswerEntity answerEntity : ansList) {
      AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
      answerDetailsResponse.setId(answerEntity.getUuid());
      answerDetailsResponse.setQuestionContent(answerEntity.getQuestionsEntity().getContent());
      answerDetailsResponse.setAnswerContent(answerEntity.getAns());
      list.add(answerDetailsResponse);
    }
    return new ResponseEntity<List<AnswerDetailsResponse>>(list, HttpStatus.OK);
  }
}
