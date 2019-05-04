package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Data
@Transactional(propagation = Propagation.REQUIRED)
public class UserBusinessService {

  @Autowired private UserDao userDao;

  @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

  // @Autowired private JwtTokenProvider jwtTokenProvider;

  public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {

    //Check if the user exists for given username
    UserEntity existingUserEntity = userDao.findUserByUserName(userEntity.getUsername());
    if(existingUserEntity != null){
      throw new SignUpRestrictedException("SGR-001" , "Try any other Username, this Username has already been taken");
    }

    //Check if the user exists for given email
    existingUserEntity = userDao.findUserByEmail(userEntity.getEmail());
    if(existingUserEntity != null){
      throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
    }

    String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
    userEntity.setSalt(encryptedText[0]);
    userEntity.setPassword(encryptedText[1]);

    return userDao.createUser(userEntity);
  }

  public UserAuthTokenEntity userLogin(String username, String password) throws AuthenticationFailedException {

        // validate username
        UserEntity userEntity = userDao.findUserByUserName(username);
    // no null check here . vimp to do null check on objects befor eusing them
    // encryptand validate password
    if (userEntity != null) {
      String encryptedPassword =
          passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
      if (encryptedPassword.equals(userEntity.getPassword())) {
        // generate and persist token
        final ZonedDateTime issuedTime = ZonedDateTime.now();
        final ZonedDateTime expiryTime = ZonedDateTime.now().plusHours(5);
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);
        String accessToken =
            jwtTokenProvider.generateToken(userEntity.getUuid(), issuedTime, expiryTime);

        UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
        userAuthTokenEntity.setAccess_token(accessToken);
        userAuthTokenEntity.setUsers(userEntity);
        userAuthTokenEntity.setLoginAt(issuedTime);
        //userAuthTokenEntity.setLogoutAt(expiryTime); shall be set after sign out
        userAuthTokenEntity.setExpiresAt(expiryTime); // why 2 columns
        userAuthTokenEntity.setUuid("login end url");
        userDao.createAuthToken(userAuthTokenEntity);   // UserAuthtoken should be persisted in the DB for future reference
        return userAuthTokenEntity;
      } else {
          throw new AuthenticationFailedException("ATH-002", "Password failed");
      }
      }
      else{
        throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
    }


 //login-logout auditing is done in user-auth-token-entity
    public UserAuthTokenEntity signOut(String access_token) throws SignOutRestrictedException {
      UserAuthTokenEntity userAuthToken = userDao.getAuthToken(access_token); //Fetching authtoken entity
      // Checking if the Access token entered matches the Access token in the DataBase and null check
        if (userAuthToken!=null && access_token.equals(userAuthToken.getAccess_token()))   
        {
          final ZonedDateTime now = ZonedDateTime.now();
          userAuthToken.setLogoutAt(now);   // Setting the Logout Time of the user
          return userAuthToken;
        } else {
          throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
    }


  public UserEntity deleteUserByUuid(final String userUuid, final String authorization) throws AuthorizationFailedException, UserNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(authorization);
    if(userAuthTokenEntity == null){
      throw new AuthorizationFailedException("ATHR-001","User has not signed in");
    }

    //Check user signed out condition
    final ZonedDateTime loggedOutTime = userAuthTokenEntity.getLogoutAt();
    final ZonedDateTime now = ZonedDateTime.now();
    if(loggedOutTime != null && now.isAfter(loggedOutTime)){
      throw new AuthorizationFailedException("ATHR-002","User is signed out");
    }
    UserEntity user = userAuthTokenEntity.getUsers();
    if(("nonadmin").equals(user.getRole())){
      throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
    }

    //Check if the user exists for the given uuid
    UserEntity userToDelete = userDao.getUserbyUuid(userUuid);

    if(userToDelete == null){
      throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
    }

    return userDao.deleteUser(userToDelete);
  }

    /*
    This method is used to fetch all the details of a signed in and Authorized user
     */
   public UserEntity getUserDetails(final String userUuid, final String access_token) throws AuthorizationFailedException, UserNotFoundException {
      final UserAuthTokenEntity userAuthToken = userDao.getAuthToken(access_token);
      final UserEntity userEntity = userDao.getUserbyUuid(userUuid);
      if (userAuthToken==null) {    // If accessToken does not exist in the Database
        throw new AuthorizationFailedException("ATHR-001", "User has not Signed in");
      } else if (userEntity==null) {    // If user UUID does not exist in the Database
        throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        // Checking if user has signed out
      } else if (userAuthToken.getLogoutAt()!=null && userAuthToken.getLogoutAt().isBefore(ZonedDateTime.now())) {
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
      } else
        return userEntity;
    }
}
