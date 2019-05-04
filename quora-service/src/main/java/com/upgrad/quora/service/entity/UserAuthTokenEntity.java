package com.upgrad.quora.service.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "USER_AUTH")
@NamedQueries({
        @NamedQuery(name = "userAuthTokenByAccessToken" , query = "select ut from UserAuthTokenEntity ut where ut.access_token = :access_token")
})

public class UserAuthTokenEntity {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //@Size()
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  //@Size()
  private String uuid;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name="USER_ID")
  @NotNull
  //@Size()
  private UserEntity users; //column takes primary key of users

  @Column(name = "ACCESS_TOKEN")
  @NotNull
  //@Size()
  private String access_token;

  @Column(name = "EXPIRES_AT")
  @NotNull
  //@Size()
  private ZonedDateTime expiresAt;

  @Column(name = "LOGIN_AT")
  @NotNull
  //@Size()
  private ZonedDateTime loginAt;

  @Column(name = "LOGOUT_AT")
  //@Size()
  private ZonedDateTime logoutAt;
}
