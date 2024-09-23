package com.team8504.csgo2web.entity;


public class Comment {

  private long id;
  private long comId;
  private long newsId;
  private long userId;
  private String cc;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getComId() {
    return comId;
  }

  public void setComId(long comId) {
    this.comId = comId;
  }


  public long getNewsId() {
    return newsId;
  }

  public void setNewsId(long newsId) {
    this.newsId = newsId;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getCc() {
    return cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

}
