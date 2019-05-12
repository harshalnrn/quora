package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class UserDao {

  @PersistenceContext private EntityManager entityManager;

  /**
   * This method persists a user in the Database
   *
   * @param userEntity
   * @return persisted user entity
   */
  public UserEntity createUser(UserEntity userEntity) {
    entityManager.persist(userEntity);
    return userEntity;
  }

  /**
   * This method finds a user by username with the help of a Named Query defined in the User Entity
   * class
   *
   * @param userName
   * @return Single User Entity
   */
  public UserEntity findUserByUserName(String userName) {
    try {
      TypedQuery<UserEntity> userByUserNameQuery =
          entityManager.createNamedQuery("findByUsername", UserEntity.class);
      userByUserNameQuery.setParameter("userByUserName", userName);
      return userByUserNameQuery.getSingleResult();
    } catch (NoResultException nrex) {
      return null;
    }
  }

  /**
   * This method finds a user by email with the help of a Named Query defined in the User Entity
   * class
   *
   * @param email
   * @return Single User Entity
   */
  public UserEntity findUserByEmail(String email) {
    try {
      TypedQuery<UserEntity> userByEmailQuery =
          entityManager.createNamedQuery("findByEmail", UserEntity.class);
      userByEmailQuery.setParameter("userByEmail", email);
      return userByEmailQuery.getSingleResult();
    } catch (NoResultException nrex) {
      return null;
    }
  }

  /**
   * This methods persists the UserAuthToken in the Database when the user is successfully Signed in
   *
   * @param userAuthTokenEntity
   * @return Single UserAuthtoken Entity
   */
  public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
    entityManager.persist(userAuthTokenEntity);
    return userAuthTokenEntity;
  }

  /**
   * This method checks whether the Access token is present in the Database, using a Named Query
   * defined in the UserAuthToken Entity class
   *
   * @param access_token
   * @return Single UserAuthtoken Entity
   */
  public UserAuthTokenEntity getAuthToken(String access_token) {
    try {
      return entityManager
          .createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
          .setParameter("access_token", access_token)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method removes/deletes a User Entity record from the Database
   *
   * @param userEntity
   * @return UserEntity
   */
  public UserEntity deleteUser(UserEntity userEntity) {
    entityManager.remove(userEntity);
    return userEntity;
  }

  /**
   * This method takes a User UUID as a parameter and checks whether the UUID is present in the
   * Database, using a Named Query defined in the User Entity class
   *
   * @param userUuid
   * @return
   */
  public UserEntity getUserbyUuid(final String userUuid) {
    try {
      return entityManager
          .createNamedQuery("userByUuid", UserEntity.class)
          .setParameter("uuid", userUuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
