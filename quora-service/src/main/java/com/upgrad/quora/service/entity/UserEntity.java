package com.upgrad.quora.service.entity;

import javax.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(
    name = "USERS",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")) // public schema
@NamedQueries({
  @NamedQuery(
      name = "findByUsername",
      query = "select u from UserEntity u where u.userName=:userByUserName"),
  @NamedQuery(
      name = "findByEmail",
      query = "select u from UserEntity u where u.email=:userByEmail"),
  @NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid")
})
public class UserEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  @Size(max = 200)
  private String uuid;

  @Column(name = "firstname")
  @NotNull
  @Size(max = 30)
  private String firstName;

  @Column(name = "lastname")
  @NotNull
  @Size(max = 30)
  private String lastName;

  @Column(name = "username")
  @NotNull
  @Size(max = 30)
  private String userName;

  @Column(name = "email")
  @NotNull
  @Size(max = 50)
  private String email;

  @Column(name = "password")
  @NotNull
  @Size(max = 255)
  private String password;

  @Column(name = "salt")
  @NotNull
  @Size(max = 200)
  private String salt;

  @Column(name = "country")
  @Size(max = 30)
  private String country;

  @Column(name = "aboutme")
  @Size(max = 50)
  private String aboutMe;

  @Column(name = "dob")
  @Size(max = 30)
  private String dob;

  @Column(name = "role")
  @Size(max = 30)
  private String role;

  @Column(name = "contactnumber")
  @Size(max = 30)
  private String contactNumber;

  @OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<AnswerEntity> answersList;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<QuestionsEntity> questionsList;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String username) {
    this.userName = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getAboutMe() {
    return aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public List<AnswerEntity> getAnswersList() {
    return answersList;
  }

  public void setAnswersList(List<AnswerEntity> answersList) {
    this.answersList = answersList;
  }

  public List<QuestionsEntity> getQuestionsList() {
    return questionsList;
  }

  public void setQuestionsList(
      List<QuestionsEntity> questionsList) {
    this.questionsList = questionsList;
  }
}
