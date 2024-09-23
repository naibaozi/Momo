package com.team8504.csgo2web.entity;


public class Teaching {

  private long id;
  private long tId;
  private long classtabId;
  private String tTitle;
  private String tDesc;
  private String tThumb;
  private java.sql.Date tDate;
  private String tAuthor;
  private String tContent;
  private String tImg;
  private String tTag;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getTId() {
    return tId;
  }

  public void setTId(long tId) {
    this.tId = tId;
  }


  public long getClasstabId() {
    return classtabId;
  }

  public void setClasstabId(long classtabId) {
    this.classtabId = classtabId;
  }


  public String getTTitle() {
    return tTitle;
  }

  public void setTTitle(String tTitle) {
    this.tTitle = tTitle;
  }


  public String getTDesc() {
    return tDesc;
  }

  public void setTDesc(String tDesc) {
    this.tDesc = tDesc;
  }


  public String getTThumb() {
    return tThumb;
  }

  public void setTThumb(String tThumb) {
    this.tThumb = tThumb;
  }


  public java.sql.Date getTDate() {
    return tDate;
  }

  public void setTDate(java.sql.Date tDate) {
    this.tDate = tDate;
  }


  public String getTAuthor() {
    return tAuthor;
  }

  public void setTAuthor(String tAuthor) {
    this.tAuthor = tAuthor;
  }


  public String getTContent() {
    return tContent;
  }

  public void setTContent(String tContent) {
    this.tContent = tContent;
  }


  public String getTImg() {
    return tImg;
  }

  public void setTImg(String tImg) {
    this.tImg = tImg;
  }


  public String getTTag() {
    return tTag;
  }

  public void setTTag(String tTag) {
    this.tTag = tTag;
  }

}
