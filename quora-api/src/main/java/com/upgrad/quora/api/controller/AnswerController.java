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

@RestController
public class AnswerController {

  @Autowired private AnswerBusinessService answerBusinessService;

  // This method will be called when the request pattern is of type
  // /question/{questionId}/answer/create and incoming request is of type POST
  // This method receives questionUuid whose answer should be stored in the database, accessToken of
  // the user creating the answer and answer string
  // This method creates the AnswerEntity object and populates it with
  // - answer string from incoming request
  // - generates and sets the UUID
  // - sets the current time
  // Sends the answerentity object to business logic to be persisted in the database
  // On success, returns the answer UUID with message ANSWER CREATED and Http Status code 200
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

  // This method will be called when the request pattern is /answer/edit/{answerId} and incoming
  // request is of type PUT
  // This method receives the answerUuid of the answer which should be edited, accessToken of the
  // user performing the operation and the new answer content
  // This method calls the business logic method to update the answer in the database
  // On success, returns the answer UUID with message ANSWER EDITED and Http Status code 200
  // throws AuthorizationFailedException , AnswerNotFoundException
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
    /*
    This method will be called when the request pattern is /answer/delete/{answerId}
    This method receives the answer Id of the answer which is to be deleted and the access Token of the logged in user
    It accepts an incoming HTTP Verb type DELETE and produces a JSON response on successfully deleting the answer in a
    Response Entity<T> type class provided by Java Spring framework along with HTTP Status Code 200 containing the answer UUID
    and the status as "ANSWER DELETED". Further, this method calls the Business logic in the Service layer to delete the answer
    in the Database & throws AuthorizationFailedException and AnswerNotFoundException as exception cases
     */
    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer( @PathVariable("answerId") final String answerUuid,
                                                              @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {
        answerBusinessService.deleteAnswer(answerUuid,accessToken);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerUuid).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }


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
