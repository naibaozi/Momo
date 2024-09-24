package com.team8504.csgo2web.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class News {

  private long nId;
  private long cId;
  private String nContent;
  private java.sql.Date nCtime;
  private String nAuthor;
  private String nAddress;
  private String nImg;
  private String nTitle;
  private String nDesc;
  private String nThumb;


}
