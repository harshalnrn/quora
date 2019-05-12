package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired private UserBusinessService userBusinessService;

  /**
   * This method accepts an HTTP method of DELETE type & is used to delete the details corresponding
   * to a specific user by an Admin user, using the User UUID of the user to be deleted and the
   * access Token of the Admin user. These parameters are passed to the Service layer where the
   * business logic implementation takes place. Only an admin is authorized to access this endpoint.
   * Response Entity generic class provided by Spring is used to map the UserDeleteResponse as an
   * object. It produces a JSON Response, with HTTP status code as = OK(200) and corresponding
   * message
   *
   * @param userUuid
   * @param authorization
   * @return User Delete Response model, HTTP status in a Response Entity object
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   */
  @RequestMapping(
      method = RequestMethod.DELETE,
      path = "/user/{userId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDeleteResponse> deleteUserByUuid(
      @PathVariable("userId") final String userUuid,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, UserNotFoundException {

    UserEntity deltedUser = userBusinessService.deleteUserByUuid(userUuid, authorization);
    UserDeleteResponse userDeleteResponse =
        new UserDeleteResponse().id(deltedUser.getUuid()).status("USER SUCCESSFULLY DELETED");
    return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
  }
}
