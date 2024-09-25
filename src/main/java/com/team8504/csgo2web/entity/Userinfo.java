package com.team8504.csgo2web.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Userinfo {

  private long uId;
  private String uNickname;
  private String uPsd;
  private String uEmail;
  private java.sql.Date uCreateTime;
  private String uAvatar;
  private String uSex;



}
