package com.team8504.csgo2web.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Teaching {

  private Integer tId;
  private Integer cId;
  private String tTitle;
  private String tDesc;
  private String tThumb;
  private java.sql.Date tDate;
  private String tAuthor;
  private String tContent;
  private String tImg;
  private String tTag;
  private String tTagname;



}
