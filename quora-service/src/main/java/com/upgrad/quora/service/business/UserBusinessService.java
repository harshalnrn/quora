package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
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

  public UserEntity signUp(UserEntity userEntity) {

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
        ZonedDateTime issuedTime = ZonedDateTime.now();
        ZonedDateTime expiryTime = ZonedDateTime.now().plusHours(5);
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);
        String accessToken =
            jwtTokenProvider.generateToken(userEntity.getUuid(), issuedTime, expiryTime);

        UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
        userAuthTokenEntity.setAccess_token(accessToken);
        userAuthTokenEntity.setUsers(userEntity);
        userAuthTokenEntity.setLoginAt(issuedTime);
        userAuthTokenEntity.setLogoutAt(expiryTime);
        userAuthTokenEntity.setExpiresAt(expiryTime); // why 2 columns
        userAuthTokenEntity.setUuid("login end url");
        return userAuthTokenEntity;
      } else {
          throw new AuthenticationFailedException("ATH-001", "This username does not exist");
      }
      }
      else{
        throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }
}
