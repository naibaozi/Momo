package com.team8504.csgo2web.entity;


public class News {

  private long id;
  private long nId;
  private String classtabId;
  private String nContent;
  private java.sql.Date nCtime;
  private String nAuthor;
  private String nAddress;
  private String nImg;
  private String nTitle;
  private String nDesc;
  private String nThumb;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getNId() {
    return nId;
  }

  public void setNId(long nId) {
    this.nId = nId;
  }


  public String getClasstabId() {
    return classtabId;
  }

  public void setClasstabId(String classtabId) {
    this.classtabId = classtabId;
  }


  public String getNContent() {
    return nContent;
  }

  public void setNContent(String nContent) {
    this.nContent = nContent;
  }


  public java.sql.Date getNCtime() {
    return nCtime;
  }

  public void setNCtime(java.sql.Date nCtime) {
    this.nCtime = nCtime;
  }


  public String getNAuthor() {
    return nAuthor;
  }

  public void setNAuthor(String nAuthor) {
    this.nAuthor = nAuthor;
  }


  public String getNAddress() {
    return nAddress;
  }

  public void setNAddress(String nAddress) {
    this.nAddress = nAddress;
  }


  public String getNImg() {
    return nImg;
  }

  public void setNImg(String nImg) {
    this.nImg = nImg;
  }


  public String getNTitle() {
    return nTitle;
  }

  public void setNTitle(String nTitle) {
    this.nTitle = nTitle;
  }


  public String getNDesc() {
    return nDesc;
  }

  public void setNDesc(String nDesc) {
    this.nDesc = nDesc;
  }


  public String getNThumb() {
    return nThumb;
  }

  public void setNThumb(String nThumb) {
    this.nThumb = nThumb;
  }

  @Override
  public String toString() {
    return "News{" +
            "id=" + id +
            ", nId=" + nId +
            ", classtabId='" + classtabId + '\'' +
            ", nContent='" + nContent + '\'' +
            ", nCtime=" + nCtime +
            ", nAuthor='" + nAuthor + '\'' +
            ", nAddress='" + nAddress + '\'' +
            ", nImg='" + nImg + '\'' +
            ", nTitle='" + nTitle + '\'' +
            ", nDesc='" + nDesc + '\'' +
            ", nThumb='" + nThumb + '\'' +
            '}';
  }
}
