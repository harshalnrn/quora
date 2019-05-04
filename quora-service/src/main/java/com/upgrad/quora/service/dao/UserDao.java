package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import lombok.Data;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Data
@Repository
public class UserDao {

  @PersistenceContext private EntityManager entityManager;

  public UserEntity createUser(UserEntity userEntity) {
    entityManager.persist(userEntity);
    return userEntity;
  }

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

  public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity){
    entityManager.persist(userAuthTokenEntity);
    return userAuthTokenEntity;
  }

  public UserAuthTokenEntity getAuthToken(String access_token){
    try {
      return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
              .setParameter("access_token",access_token)
              .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  public UserEntity deleteUser(UserEntity userEntity){
    entityManager.remove(userEntity);
    return  userEntity;
  }

  public UserEntity getUserbyUuid(final String userUuid) {
    try {
      return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid)
              .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
