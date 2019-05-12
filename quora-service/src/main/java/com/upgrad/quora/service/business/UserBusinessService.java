package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import com.upgrad.quora.service.common.GenericExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserBusinessService {

  @Autowired private UserDao userDao;

  @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

  /**
   * This method takes a UserEntity object as a parameter and encrypts the password of the user
   * then, passes it to the DAO layer to persist the user in the Database. Checks whether the
   * username or email provided by the user already exists in the Database & accordingly throws
   * Exceptions
   *
   * @param userEntity
   * @return User Entity persisted in the DB
   * @throws SignUpRestrictedException
   */
  public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {

    // Check if the user exists for given username
    UserEntity existingUserEntity = userDao.findUserByUserName(userEntity.getUsername());
    if (existingUserEntity != null) {
      throw new SignUpRestrictedException(
          GenericExceptionCode.SGR_001.getCode(), GenericExceptionCode.SGR_001.getDescription());
    }

    // Check if the user exists for given email
    existingUserEntity = userDao.findUserByEmail(userEntity.getEmail());
    if (existingUserEntity != null) {
      throw new SignUpRestrictedException(
          GenericExceptionCode.SGR_002.getCode(), GenericExceptionCode.SGR_002.getDescription());
    }

    String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
    userEntity.setSalt(encryptedText[0]);
    userEntity.setPassword(encryptedText[1]);

    return userDao.createUser(userEntity);
  }

  /**
   * This method takes the username and the password decrypted by the Controller, encrypts the
   * password using the encrypt method in the Password Cryptography Provider class and then verifies
   * it with the existing password in the DB. Generates an JWT Token (Access Token),issue time &
   * Expiry time using the Jwt Token Provider class and generates a UserAuthToken entity using the
   * corresponding fields and passes it to the DAO layer to persist in the Database
   *
   * @param username
   * @param password
   * @return UserAuthToken Entity persisted in the DB
   * @throws AuthenticationFailedException
   */
  public UserAuthTokenEntity userLogin(String username, String password)
      throws AuthenticationFailedException {

    // validate username
    UserEntity userEntity = userDao.findUserByUserName(username);
    // no null check here . vimp to do null check on objects before using them
    // encrypt and validate password
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
        userAuthTokenEntity.setExpiresAt(expiryTime); // why 2 columns
        userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
        userDao.createAuthToken(
            userAuthTokenEntity); // UserAuthtoken should be persisted in the DB for future
        // reference
        return userAuthTokenEntity;
      } else {
        throw new AuthenticationFailedException(
            GenericExceptionCode.ATH_002.getCode(), GenericExceptionCode.ATH_002.getDescription());
      }
    } else {
      throw new AuthenticationFailedException(
          GenericExceptionCode.ATH_001.getCode(), GenericExceptionCode.ATH_001.getDescription());
    }
  }

  /**
   * This method updates the Log-out time of the user when he Signs out from the application post verification that the
   * Access token of the user exists in the Database and matches with access token entered by the Signed in user
   * @param access_token
   * @return User Auth Token Entity object
   * @throws SignOutRestrictedException
   */
  // login-logout auditing is done in user-auth-token-entity
  public UserAuthTokenEntity signOut(String access_token) throws SignOutRestrictedException {
    UserAuthTokenEntity userAuthToken =
        userDao.getAuthToken(access_token); // Fetching authtoken entity
    // Checking if the Access token entered matches the Access token in the DataBase and null check
    if (userAuthToken != null && access_token.equals(userAuthToken.getAccess_token())) {
      final ZonedDateTime now = ZonedDateTime.now();
      userAuthToken.setLogoutAt(now); // Setting the Logout Time of the user
      return userAuthToken;
    } else {
      throw new SignOutRestrictedException(
          GenericExceptionCode.SGR_001_SIGNOUT.getCode(),
          GenericExceptionCode.SGR_001_SIGNOUT.getDescription());
    }
  }

  /**
   * This method is used to delete the details of a signed in and Authorized user by an Admin user.
   * Checks if the Auth Token is present in the Database, the user UUID is present in the Database,
   * the user has signed out or not, & if a Non-admin user is trying to delete a user. On
   * successfull Authorization, the user Entity is passed to the DAO layer to be removed
   *
   * @param userUuid
   * @param authorization
   * @return User Entity
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   */
  public UserEntity deleteUserByUuid(final String userUuid, final String authorization)
      throws AuthorizationFailedException, UserNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(authorization);
    // If accessToken does not exist in the Database,̥throws
    // AuthorizationFailedException
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException(
          GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
    }

    // Checking user signed out condition
    final ZonedDateTime loggedOutTime = userAuthTokenEntity.getLogoutAt();
    if (hasUserSignedOut(loggedOutTime)) {
      throw new AuthorizationFailedException(
          GenericExceptionCode.ATHR_002_USER_DELETE.getCode(),
          GenericExceptionCode.ATHR_002_USER_DELETE.getDescription());
    }
    UserEntity user = userAuthTokenEntity.getUsers();
    if (("nonadmin").equals(user.getRole())) {
      throw new AuthorizationFailedException(
          GenericExceptionCode.ATHR_003_ADMIN.getCode(),
          GenericExceptionCode.ATHR_003_ADMIN.getDescription());
    }

    // Check if the user exists for the given uuid
    UserEntity userToDelete = userDao.getUserbyUuid(userUuid);

    if (userToDelete == null) {
      throw new UserNotFoundException(
          GenericExceptionCode.USR_001.getCode(), GenericExceptionCode.USR_001.getDescription());
    }

    return userDao.deleteUser(userToDelete);
  }

  /**
   * This method is used to fetch all the details of a signed in and Authorized user. It takes the
   * Access Token of the Logged-in User and the User UUID from the Controller Method. It fetches the
   * User Details from the DAO class and returns it to the Controller method
   *
   * @param userUuid
   * @param access_token
   * @return Details of the User in User Entity object
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   */
  public UserEntity getUserDetails(final String userUuid, final String access_token)
      throws AuthorizationFailedException, UserNotFoundException {
    final UserAuthTokenEntity userAuthToken = userDao.getAuthToken(access_token);
    final UserEntity userEntity = userDao.getUserbyUuid(userUuid);
    // If accessToken does not exist in the Database,̥throws
    // AuthorizationFailedException
    if (userAuthToken == null) {
      throw new AuthorizationFailedException(
          GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
    }
    // If user UUID does not exist in the Database,throws UserNotFoundException
    else if (userEntity == null) {
      throw new UserNotFoundException(
          GenericExceptionCode.USR_001.getCode(), GenericExceptionCode.USR_001.getDescription());
    }
    // Checking if user has signed out, then throws AuthorizationFailedException
    else if (hasUserSignedOut(userAuthToken.getLogoutAt())) {
      throw new AuthorizationFailedException(
          GenericExceptionCode.ATHR_002_GET.getCode(),
          GenericExceptionCode.ATHR_002_GET.getDescription());
    } else return userEntity;
  }

  /**
   * Checks if the user has signed out by comparing if the current time is after the loggedOutTime
   * received by the method. Returns true if the current-time is after loggedOutTime(sign-out has
   * happened), false otherwise
   *
   * @param loggedOutTime
   * @return boolean value true/false
   */
  public boolean hasUserSignedOut(ZonedDateTime loggedOutTime) {
    return (loggedOutTime != null && ZonedDateTime.now().isAfter(loggedOutTime));
  }
}
