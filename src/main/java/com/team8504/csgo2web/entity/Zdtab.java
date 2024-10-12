package com.team8504.csgo2web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.entity
 * Project: CSGO2WEB
 * Date: 2024/10/12/下午4:07
 * Version 0.0
 */
@Data
@ToString
public class Zdtab {
    private Integer zId;
    private String zName;
    private String zBigimg;
    private String zSmallimg;
    private String zDesc;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date zTime;
    private String zKh;
}
