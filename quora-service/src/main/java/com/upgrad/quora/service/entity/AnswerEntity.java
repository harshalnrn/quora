package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ANSWER")
@NamedQueries({
  @NamedQuery(name = "findAnswerByUuid", query = "select a from AnswerEntity a where uuid = :uuid"),
  @NamedQuery(
      name = "findAnswersByQuuid",
      query = "select a from AnswerEntity a where a.questionsEntity.uuid=:question_id")
})
public class AnswerEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  @Size(max = 200)
  private String uuid;

  @Column(name = "ans")
  @NotNull
  @Size(max = 255)
  private String ans;

  @Column(name = "date")
  @NotNull
  private ZonedDateTime date;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @NotNull
  private UserEntity users; // takes primary key of users

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  @NotNull
  private QuestionsEntity questionsEntity;

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

  public String getAns() {
    return ans;
  }

  public void setAns(String ans) {
    this.ans = ans;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUsers() {
    return users;
  }

  public void setUsers(UserEntity users) {
    this.users = users;
  }

  public QuestionsEntity getQuestionsEntity() {
    return questionsEntity;
  }

  public void setQuestionsEntity(QuestionsEntity questionsEntity) {
    this.questionsEntity = questionsEntity;
  }
}
