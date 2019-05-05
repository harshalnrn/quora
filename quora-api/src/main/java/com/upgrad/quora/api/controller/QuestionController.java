package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionEditResponse;
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
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(
            QuestionRequest questionRequest, @RequestHeader("authorisationHeader") String accessToken) throws AuthorizationFailedException {
        QuestionsEntity questionsEntity = new QuestionsEntity();
        questionsEntity.setContent(questionRequest.getContent());
        questionsEntity.setDate(ZonedDateTime.now());
        // questionsEntity.setUserEntity();  //set in service by getting user from authTokenEntity
        questionsEntity.setUuid(UUID.randomUUID().toString());
//note : token is like authorised user session, where its logged in users safe identity
        questionBusinessService.createQuestionService(questionsEntity, accessToken);

        QuestionResponse reponse = new QuestionResponse();
        reponse.setId(questionsEntity.getUuid());
        reponse.setStatus("QUESTION CREATED");
        ResponseEntity<QuestionResponse> responseEntity = new ResponseEntity(reponse, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "question/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion
            (@PathVariable("questionId") String quesUuid,
             @RequestHeader("authorization") String accessToken,
             QuestionRequest questionRequest)throws AuthorizationFailedException, InvalidQuestionException {

        QuestionsEntity questionsEntity = questionBusinessService.editQuestionService(quesUuid,accessToken);
        questionsEntity.setContent(questionRequest.getContent());
        questionsEntity.setDate(ZonedDateTime.now());
        questionBusinessService.updateQuestion(questionsEntity);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(quesUuid).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.CREATED);
    }
}