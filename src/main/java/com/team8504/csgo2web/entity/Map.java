package com.team8504.csgo2web.entity;


public class Map {

  private long id;
  private long classtabId;
  private String mImg;
  private String mTag;
  private String mDesc;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getClasstabId() {
    return classtabId;
  }

  public void setClasstabId(long classtabId) {
    this.classtabId = classtabId;
  }


  public String getMImg() {
    return mImg;
  }

  public void setMImg(String mImg) {
    this.mImg = mImg;
  }


  public String getMTag() {
    return mTag;
  }

  public void setMTag(String mTag) {
    this.mTag = mTag;
  }


  public String getMDesc() {
    return mDesc;
  }

  public void setMDesc(String mDesc) {
    this.mDesc = mDesc;
  }

}
