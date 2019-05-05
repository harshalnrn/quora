package com.upgrad.quora.service.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@Table(name = "USERS",uniqueConstraints = @UniqueConstraint(columnNames = "email")) //public schema
@NamedQueries({
@NamedQuery(name="findByUsername", query = "select u from UserEntity u where u.username=:userByUserName"),
@NamedQuery(name="findByEmail",query="select u from UserEntity u where u.email=:userByEmail"),
@NamedQuery(name="findByUuid",query="select u from UserEntity u where u.uuid=:uuid"),
@NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid")})

public class UserEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
 // @Size()
  private Integer id;

  @Column(name = "uuid")
  @NotNull
 // @Size()
  private String uuid;

  @Column(name = "firstname")
  @NotNull
  //@Size()
  private String firstName;

  @Column(name = "lastname")
  @NotNull
  //@Size()
  private String lastName;

  @Column(name = "username")
  @NotNull
  //@Size()
  private String username;

 @Column(name = "email")
  @NotNull
 //@Size()

  private String email;

  @Column(name = "password")
  @NotNull
  //@Size()
  private String password;

  @Column(name = "salt")
  @NotNull
  //@Size()
  private String salt;

  @Column(name = "country")
  //@Size()
  private String country;

  @Column(name = "aboutme")
  //@Size()
  private String aboutme;

  @Column(name = "dob")
  //@Size()
  private String dob;

  @Column(name = "role")
  //@Size()
  private String role;

  @Column(name = "contactnumber")
  //@Size()
  private String contactNumber;

  @OneToMany
  @JoinColumn(name="user_id")
  private List<AnswerEntity> answersList;

  @OneToMany
  @JoinColumn(name="user_id")
  private List<QuestionsEntity> questionsList;
}
