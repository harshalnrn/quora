package com.upgrad.quora.service.entity;

import javax.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "QUESTION")
@NamedQueries({
        @NamedQuery(name="allQuestions",query ="select q from QuestionsEntity q"),
        @NamedQuery(name = "QuestionByUuid" , query = "select q from QuestionsEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "findQuestionsByUserId" , query = "select q from QuestionsEntity q where q.userEntity.uuid = :userUuid")
})

public class QuestionsEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "content")
    @NotNull
    @Size(max = 500)
    private String content;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private UserEntity userEntity;

   @OneToMany(mappedBy = "questionsEntity",fetch = FetchType.LAZY)
   @OnDelete(action=OnDeleteAction.CASCADE)
    private List<AnswerEntity> answersOfQuestion;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

}