package com.team8504.csgo2web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ToString
public class Teaching {

  private Integer tId;
  private Integer cId;
  private String tTitle;
  private String tDesc;
  private String tThumb;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private java.util.Date tDate;
  private String tAuthor;
  private String tContent;
  private String tImg;
  private String tTag;
  private String tTagname;
  private String tAddress;



}
