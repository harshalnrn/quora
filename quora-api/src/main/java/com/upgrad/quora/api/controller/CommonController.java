package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

  @Autowired private UserBusinessService userBusinessService;

  /**
   * This method accepts an HTTP method of GET type & is used to get the details corresponding to a
   * specific user, using the User UUID and the access Token of the Signed in user. The parameters
   * are passed to the Service layer where the business logic implementation takes place. The
   * Endpoint corresponding to this method can be accessed by any user in the Application. Response
   * Entity generic class provided by Spring is used to map the UserDetailResponse as an object. It
   * produces a JSON Response, with HTTP status code as = OK(200)
   *
   * @param userUuid
   * @param authorization
   * @return User Details Response model with HTTP status in a Response Entity object
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   */
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/userprofile/{userId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDetailsResponse> getUser(
      @PathVariable("userId") final String userUuid,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, UserNotFoundException {
    final UserEntity userEntity = userBusinessService.getUserDetails(userUuid, authorization);
    UserDetailsResponse userDetailsResponse =
        new UserDetailsResponse()
            .firstName(userEntity.getFirstName())
            .lastName(userEntity.getLastName())
            .emailAddress(userEntity.getEmail())
            .aboutMe(userEntity.getAboutMe())
            .userName(userEntity.getUsername())
            .dob(userEntity.getDob())
            .contactNumber(userEntity.getContactNumber())
            .country(userEntity.getCountry());
    return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
  }
}
