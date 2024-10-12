package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Dy;

import java.util.List;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.dao
 * Project: CSGO2WEB
 * Date: 2024/10/12/下午4:08
 * Version 0.0
 */
public interface DyDao {
    public List<Dy> getDyList(Integer zId);
    public List<Dy> getDy(Integer dId);
}
