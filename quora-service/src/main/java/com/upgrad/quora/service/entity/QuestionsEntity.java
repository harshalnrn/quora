package com.upgrad.quora.service.entity;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "QUESTION")
@NamedQueries({
        @NamedQuery(name="allQuestions",query ="select q from QuestionsEntity q"),
        @NamedQuery(name = "findQuestionByUuid" , query = "Select q from QuestionsEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "findQuestionsByUserId" , query = "select q from QuestionsEntity q where q.userEntity.uuid = :userUuid")
})
public class QuestionsEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OnDelete(action=OnDeleteAction.CASCADE)
    //@Size()
    private Integer id;

    @Column(name = "uuid")
    @NotNull
   // @Size()
    private String uuid;

    @Column(name = "content")
    @NotNull
    //@Size()
    private String content;

    @Column(name = "date")
    @NotNull
    //@Size()
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    //@Size()
    private UserEntity userEntity;

   /* @OneToMany
    @JoinColumn(name = "user_id")
   //@Size()
    private List<AnswerEntity> answersOfQuestion;
    */
}