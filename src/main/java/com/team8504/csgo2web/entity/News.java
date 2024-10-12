package com.team8504.csgo2web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ToString
public class News {

  private Integer nId;
  private Integer lId;
  private String lIdname;
  private String nTitle;
  private String nDesc;
  private String nThumb;
  private String nContent;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private java.util.Date nCtime;
  private String nAuthor;
  private String nAddress;
  private String nImg;



}
