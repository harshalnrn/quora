package com.upgrad.quora.service.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;

@Entity
@Data
@Table(name = "ANSWER")
public class Answers {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Size()
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  @Size()
  private String uuid;

  @Column(name = "ans")
  @NotNull
  @Size()
  private String ans;

  @Column(name = "date")
  @NotNull
  @Size()
  private Time date;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @NotNull
  @Size()
  private Users users; // takes primary key of users

  @ManyToOne
  @JoinColumn(name = "question_id")
  @NotNull
  @Size()
  private Integer question_id;
}