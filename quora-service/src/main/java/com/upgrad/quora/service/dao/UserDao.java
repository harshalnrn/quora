package com.upgrad.quora.service.dao;

import com.oracle.jrockit.jfr.management.NoSuchRecordingException;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity findUserByUserName(String userName){
        try {
            TypedQuery<UserEntity> userByUserNameQuery = entityManager.createNamedQuery("userByUserName", UserEntity.class);
            userByUserNameQuery.setParameter("userName", userName);
            return userByUserNameQuery.getSingleResult();
        }catch(NoResultException nrex){
            return null;
        }
    }

    public UserEntity findUserByEmail(String email){
        try {
            TypedQuery<UserEntity> userByEmailQuery = entityManager.createNamedQuery("userByEmail", UserEntity.class);
            userByEmailQuery.setParameter("email", email);
            return userByEmailQuery.getSingleResult();
        }catch(NoResultException nrex){
            return null;
        }
    }
}
