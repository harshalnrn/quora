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
public class CommonController {

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.GET, path="/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUuid,
                                                       @RequestHeader("AccessToken") final String authorization)
                                                        throws AuthorizationFailedException, UserNotFoundException {
       final UserEntity userEntity = userBusinessService.getUserDetails(userUuid,authorization);
       UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).emailAddress(userEntity.getEmail()).aboutMe(userEntity.getAboutme())
                .userName(userEntity.getUsername()).dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
