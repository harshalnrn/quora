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

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/user/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<UserDeleteResponse> deleteUserByUuid(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException{

        UserEntity deltedUser = userBusinessService.deleteUserByUuid(userUuid, authorization);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deltedUser.getUuid()).status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse , HttpStatus.OK);
    }
}
