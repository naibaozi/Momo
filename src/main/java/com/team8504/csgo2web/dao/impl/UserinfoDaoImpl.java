package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.UserinfoDao;
import com.team8504.csgo2web.entity.Userinfo;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @author naibaozi
 */
@Repository
public class UserinfoDaoImpl implements UserinfoDao {
    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 保存用户信息
     *
     * @param userinfo 用户信息
     */
    @Override
    public void saveUserinfo(Userinfo userinfo){
        String sql = "insert into userinfo(u_nickname,u_psd,u_email,u_create_time,u_avatar,u_sex,u_status) values(?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,userinfo.getUNickname(),userinfo.getUPsd(),userinfo.getUEmail(),userinfo.getUCreateTime(),userinfo.getUAvatar(),userinfo.getUSex(),userinfo.getUStatus());
    }
    @Override
    public List<Userinfo> getUserinfoList(){
        String sql = "select * from userinfo";
        List<Userinfo> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Userinfo.class));
        return list;
    }
    @Override
    public Userinfo login(String uEmail, String uPsd){
        String sql = "select * from userinfo where u_email = ? and u_psd = ?";
        Userinfo userinfo = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(Userinfo.class),uEmail,uPsd);
        return userinfo;

    }

}
