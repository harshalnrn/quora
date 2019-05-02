package com.upgrad.quora.service.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;

@Entity
@Data
@Table(name = "USER_AUTH")
public class UserAuthTokens {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Size()
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  @Size()
  private String uuid;

  @JoinColumn(name="USER_ID")
  @NotNull
  @Size()
  private Users users; //column takes primary key of users

  @Column(name = "ACCESS_TOKEN")
  @NotNull
  @Size()
  private String access_token;

  @Column(name = "EXPIRES_AT")
  @NotNull
  @Size()
  private Time expiresAt;

  @Column(name = "LOGIN_AT")
  @NotNull
  @Size()
  private Time loginAt;

  @Column(name = "LOGOUT_AT")
  @Size()
  private Time logoutAt;
}
