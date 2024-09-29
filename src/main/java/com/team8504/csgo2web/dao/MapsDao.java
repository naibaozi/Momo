package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Maps;

import java.util.List;

public interface MapsDao {
    public List<Maps> getMapsList(Integer cId);
}
