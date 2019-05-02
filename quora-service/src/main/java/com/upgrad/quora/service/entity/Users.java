package com.upgrad.quora.service.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@Table(name = "USERS", schema = "quora",uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Users {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Size()
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  @Size()
  private String uuid;

  @Column(name = "firstName")
  @NotNull
  @Size()
  private String firstName;

  @Column(name = "lastName")
  @NotNull
  @Size()
  private String lastName;

  @Column(name = "userName")
  @NotNull
  @Size()
  private String username;

 @Column(name = "email")
  @NotNull
 @Size()

  private String email;

  @Column(name = "password")
  @NotNull
  @Size()
  private String password;

  @Column(name = "salt")
  @NotNull
  @Size()
  private String salt;

  @Column(name = "country")
  @Size()
  private String country;

  @Column(name = "aboutMe")
  @Size()
  private String aboutme;

  @Column(name = "dob")
  @Size()
  private String dob;

  @Column(name = "role")
  @Size()
  private String role;

  @Column(name = "contactNumber")
  @Size()
  private String contactNumber;

  @OneToMany private List<Answers> answersList;

  @OneToMany private List<Questions> questionsList;
}
