package com.upgrad.quora.service.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "ANSWER")
@NamedQueries(@NamedQuery(name = "findAnswerByUuid" , query = "select a from AnswerEntity a where uuid = :uuid"))
public class AnswerEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Size()
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    //@Size()
    private String uuid;

    @Column(name = "ans")
    @NotNull
   // @Size()
    private String ans;

    @Column(name = "date")
    @NotNull
   // @Size()
    private ZonedDateTime date;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    @NotNull
    //@Size()
    private UserEntity users; // takes primary key of users

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "question_id")
    @NotNull
    //@Size()
    private QuestionsEntity questionsEntity;
}