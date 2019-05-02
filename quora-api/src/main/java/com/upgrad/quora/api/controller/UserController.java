package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
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

  @RequestMapping(
      method = RequestMethod.POST,
      path = "/signup",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> userSignUp(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

    UserEntity userEntity = new UserEntity();
    userEntity.setUuid(UUID.randomUUID().toString());
    userEntity.setFirstName(signupUserRequest.getFirstName());
    userEntity.setLastName(signupUserRequest.getLastName());
    userEntity.setUsername(signupUserRequest.getUserName());
    userEntity.setEmail(signupUserRequest.getEmailAddress());
    userEntity.setPassword(signupUserRequest.getPassword());
    userEntity.setCountry(signupUserRequest.getCountry());
    userEntity.setAboutme(signupUserRequest.getAboutMe());
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

  @RequestMapping(                                           //spring default exception handling for internal error
      method = RequestMethod.POST,
      value = "/signin",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SigninResponse> userLogin(
      @RequestHeader("authorisationHeader") String authorisationHeader)
      throws AuthenticationFailedException {
    // decoding header
    SigninResponse signinResponse = new SigninResponse();
    byte[] decode = Base64.getDecoder().decode(authorisationHeader.split("Basic ")[1]);
    String decodedText = new String(decode); // convert byte[] to string
      System.out.print(decodedText);
    String[] credentials = decodedText.split(":");
    System.out.println(credentials[0]);
    UserAuthTokenEntity userAuthTokenEntity =
        userBusinessService.userLogin(credentials[0], credentials[1]);
    signinResponse.setId(userAuthTokenEntity.getUsers().getUuid());
    signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("accessToken", userAuthTokenEntity.getAccess_token());

    return new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);
  } // why final required with parameter
}
