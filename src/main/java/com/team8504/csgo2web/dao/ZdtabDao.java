package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Dy;
import com.team8504.csgo2web.entity.Zdtab;

import java.util.List;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.dao
 * Project: CSGO2WEB
 * Date: 2024/10/12/下午4:28
 * Version 0.0
 */
public interface ZdtabDao {
    public List<Zdtab> getZdtabList();
    public List<Zdtab> getZd(Integer zId);
}
