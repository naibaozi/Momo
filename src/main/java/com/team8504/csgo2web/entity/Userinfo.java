package com.team8504.csgo2web.entity;


public class Userinfo {

  private long userid;
  private String username;
  private String password;
  private String email;
  private java.sql.Date createTime;
  private String headpt;
  private String sex;


  public long getUserid() {
    return userid;
  }

  public void setUserid(long userid) {
    this.userid = userid;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  public java.sql.Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.sql.Date createTime) {
    this.createTime = createTime;
  }


  public String getHeadpt() {
    return headpt;
  }

  public void setHeadpt(String headpt) {
    this.headpt = headpt;
  }


  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

}
