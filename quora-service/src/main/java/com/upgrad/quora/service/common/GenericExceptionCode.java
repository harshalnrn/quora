package com.upgrad.quora.service.common;

import java.util.HashMap;
import java.util.Map;

public enum GenericExceptionCode {
  SGR_001("SGR-001", "Try any other Username, this Username has already been taken"),
  SGR_002("SGR-002", "This user has already been registered, try with any other emailId"),
  ATH_001("ATH-001", "This username does not exist"),
  ATH_002("ATH-002", "Password failed"),
  SGR_001_SIGNOUT("SGR-001", "User is not Signed in"),
  ATHR_001("ATHR-001", "User has not signed in"),
  ATHR_002_GET("ATHR-002", "User is signed out.Sign in first to get user details"),
  ATHR_002_QUES("ATHR-002", "User is signed out.Sign in first to post a question"),
  ATHR_002_USER_DELETE("ATHR-002", "User is signed out"),
  ATHR_002_QUES_GET("ATHR-002", "User is signed out.Sign in first to get all questions"),
  ATHR_002_QUES_DELETE("ATHR-002", "User is signed out.Sign in first to delete a question"),
  ATHR_002_QUES_GET_USER(
      "ATHR-002",
      "User is signed out.Sign in first to get all questions posted by a specific user"),
  ATHR_003_QUES_EDIT("ATHR-003", "Only the question owner can edit the question"),
  ATHR_003_QUES_DELETE("ATHR-003", "Only the question owner or admin can delete the question"),
  ATHR_003_ADMIN("ATHR-003", "Unauthorized Access, Entered user is not an admin"),
  USR_001("USR-001", "User with entered uuid does not exist"),
  USR_001_QUES_GET_USER(
      "USR-001", "User with entered uuid whose question details are to be seen does not exist"),
  QUES_001("QUES-001", "Entered question uuid does not exist"),
  USR_001_QUES(
      "USR-001", "User with entered uuid whose question details are to be seen does not exist"),
  QUES_001_ANS("QUES-001", "The question entered is invalid"),
  ATHR_002_ANS_CREATE("ATHR-002", "User is signed out.Sign in first to post an answer"),
  ATHR_002_ANS_EDIT("ATHR-002", "User is signed out.Sign in first to edit an answer"),
  ATHR_002_ANS_DELETE("ATHR-002", "User is signed out.Sign in first to delete an answer"),
  ATHR_002_ANS_GETALL("ATHR-002", "User is signed out.Sign in first to get the answers"),
  ATHR_003_ANS_EDIT("ATHR-003", "Only the answer owner can edit the answer"),
  ATHR_003_ANS_DELETE("ATHR-003", "Only the answer owner or admin can delete the answer"),
  ANS_001("ANS-001", "Entered answer uuid does not exist"),
  QUES_001_ANS_GETALL(
      "QUES-001", "The question with entered uuid whose details are to be seen does not exist");

  private static final Map<String, GenericExceptionCode> Lookup = new HashMap<>();

  static {
    for (GenericExceptionCode exceptionCode : GenericExceptionCode.values()) {
      Lookup.put(exceptionCode.getCode(), exceptionCode);
    }
  }

  private final String code;
  private final String description;

  private GenericExceptionCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public static GenericExceptionCode getEnum(final String code) {
    return Lookup.get(code);
  }
}
