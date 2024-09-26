package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Classtab;
import  java.util.List;

/*
* 一级栏目Dao接口
*
*
* */
public interface ClasstabDao {
    //增
    public boolean insertClasstab(Classtab classtab);
    //删
    public boolean deleteClasstab(long id);
    //改
    public boolean updateClasstab(Classtab classtab);
    //查
    public List<Classtab> getClasstabList();
}
