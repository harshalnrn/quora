package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "USER_AUTH")
@NamedQueries({
  @NamedQuery(
      name = "userAuthTokenByAccessToken",
      query = "select ut from UserAuthTokenEntity ut where ut.access_token = :access_token")
})
public class UserAuthTokenEntity {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // @Size()
  private Integer id;

  @Column(name = "uuid")
  @NotNull
  // @Size()
  private String uuid;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID")
  @NotNull
  // @Size()
  private UserEntity users; // column takes primary key of users

  @Column(name = "ACCESS_TOKEN")
  @NotNull
  // @Size()
  private String access_token;

  @Column(name = "EXPIRES_AT")
  @NotNull
  // @Size()
  private ZonedDateTime expiresAt;

  @Column(name = "LOGIN_AT")
  @NotNull
  // @Size()
  private ZonedDateTime loginAt;

  @Column(name = "LOGOUT_AT")
  // @Size()
  private ZonedDateTime logoutAt;

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

  public UserEntity getUsers() {
    return users;
  }

  public void setUsers(UserEntity users) {
    this.users = users;
  }

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public ZonedDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(ZonedDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public ZonedDateTime getLoginAt() {
    return loginAt;
  }

  public void setLoginAt(ZonedDateTime loginAt) {
    this.loginAt = loginAt;
  }

  public ZonedDateTime getLogoutAt() {
    return logoutAt;
  }

  public void setLogoutAt(ZonedDateTime logoutAt) {
    this.logoutAt = logoutAt;
  }
}
