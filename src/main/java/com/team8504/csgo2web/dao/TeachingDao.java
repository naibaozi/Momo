package com.team8504.csgo2web.dao;
import com.team8504.csgo2web.entity.Teaching;

import java.util.List;

public interface TeachingDao {
    //增
    public boolean addTeaching(Teaching teaching);
    //删
    public boolean deleteTeaching(Integer tId);
    //改
    public boolean updateTeaching(Teaching teaching);
    //查
    public List<Teaching> getTeachingList(Integer cId);

    public List<Teaching> getCourseList(Integer tId);
}
