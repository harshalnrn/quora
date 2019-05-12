package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user") // confusion with root context for rest calls (i.e default, custom)
public class UserController {

  @Autowired private UserBusinessService userBusinessService;

  /**
   * This method accepts an HTTP method of POST type and is used to sign-up a user using the
   * SignUpUserRequest model. The corresponding fields of the user are set using the Request model
   * fields. These parameters are passed to the Service layer where the business logic
   * implementation takes place. Response Entity generic class provided Spring is used to map the
   * SignupUserResponse as an object. It produces a JSON Response,with HTTP status code as =
   * CREATED(201) and corresponding message
   *
   * @param signupUserRequest
   * @return Sign up Response model & HTTP status in a Response Entity object
   * @throws SignUpRestrictedException
   */
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/signup",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> userSignUp(final SignupUserRequest signupUserRequest)
      throws SignUpRestrictedException {

    UserEntity userEntity = new UserEntity();
    userEntity.setUuid(UUID.randomUUID().toString());
    userEntity.setFirstName(signupUserRequest.getFirstName());
    userEntity.setLastName(signupUserRequest.getLastName());
    userEntity.setUsername(signupUserRequest.getUserName());
    userEntity.setEmail(signupUserRequest.getEmailAddress());
    userEntity.setPassword(signupUserRequest.getPassword());
    userEntity.setCountry(signupUserRequest.getCountry());
    userEntity.setAboutMe(signupUserRequest.getAboutMe());
    userEntity.setDob(signupUserRequest.getDob());
    userEntity.setCountry(signupUserRequest.getCountry());
    userEntity.setContactNumber(signupUserRequest.getContactNumber());
    userEntity.setRole("nonadmin");

    UserEntity createdUserEntity = userBusinessService.signUp(userEntity);

    SignupUserResponse userResponse =
        new SignupUserResponse()
            .id(createdUserEntity.getUuid())
            .status("USER SUCCESSFULLY REGISTERED");

    return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
  }

  /**
   * This method accepts an HTTP method of POST type and is used to authenticate a user on Sign in
   * The access token of the Signed in User is provided as an input and is decoded and passed to the
   * Service Layer for authentication purpose. Response Entity generic class provided Spring is used
   * to map the SigninResponse as an object. It produces a JSON Response,with HTTP status code as =
   * OK(200) and corresponding message
   *
   * @param authorisationHeader
   * @return Sign in Response model with HTTP header, HTTP status in a Response Entity object
   * @throws AuthenticationFailedException
   */
  @RequestMapping( // spring default exception handling for internal error
      method = RequestMethod.POST,
      value = "/signin",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SigninResponse> userLogin(
      @RequestHeader("authorization") String authorisationHeader)
      throws AuthenticationFailedException {
    // decoding header
    SigninResponse signinResponse = new SigninResponse();
    byte[] decode = Base64.getDecoder().decode(authorisationHeader.split("Basic ")[1]);
    String decodedText = new String(decode); // convert byte[] to string
    String[] credentials = decodedText.split(":");
    UserAuthTokenEntity userAuthTokenEntity =
        userBusinessService.userLogin(credentials[0], credentials[1]);
    signinResponse.setId(userAuthTokenEntity.getUsers().getUuid());
    signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("access-token", userAuthTokenEntity.getAccess_token());

    return new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);
  }

  /**
   * This method accepts a HTTP method of POST type and is called when a user tries to Sign out from
   * the Application. It accepts the access Token of the user to be signed out and passes it to the
   * Service Layer for Authentication purpose. Response Entity generic class provided Spring is used
   * to map the SignoutResponse as an object. It produces a JSON Response,with HTTP status code as
   * =OK(200) and corresponding message
   *
   * @param authorisationHeader
   * @return Sign Out Response model & HTTP status in a Response Entity object
   * @throws SignOutRestrictedException
   */
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/signout",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignoutResponse> userSignOut(
      @RequestHeader("authorization")
          String authorisationHeader) // Requesting the Access Token in the Request Header
      throws SignOutRestrictedException {

    UserAuthTokenEntity userAuthTokenEntity = userBusinessService.signOut(authorisationHeader);
    SignoutResponse signoutResponse = new SignoutResponse();
    signoutResponse.setId(userAuthTokenEntity.getUsers().getUuid());
    signoutResponse.setMessage("SIGNED OUT SUCCESSFULLY");
    return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
  }
}
