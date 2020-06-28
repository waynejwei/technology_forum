package com.example.technology_forum.dao;

import com.example.technology_forum.model.Special;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class specialDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /*关注某人，spec_id为被关注的人*/
    public void followPerson(Special special){
        jdbcTemplate.update("INSERT INTO special(u_id,spec_id) VALUES(?,?)",
                special.getU_id(),
                special.getSpec_id());
    }

    /*取消关注*/
    public void cancelFollowing(Special special){
        jdbcTemplate.update("DELETE FROM special WHERE u_id=? and spec_id=?",special.getU_id(),special.getSpec_id());
    }

    /*查看自己关注某人没有*/
    public Special hasFollowed(Special special){
        return (Special)jdbcTemplate.queryForObject("SELECT * FROM special WHERE u_id=? and spec_id=?",
                new Object[]{special.getU_id(),special.getSpec_id()},new BeanPropertyRowMapper<>(Special.class));
    }

    /*查看自己关注的人有哪些*/
    public List<Special> getFollowers(Special special){
        return (List<Special>)jdbcTemplate.query("SELECT * FROM special WHERE u_id=?",
                new Object[]{special.getU_id()},new BeanPropertyRowMapper<>(Special.class));
    }

    /*查看自己的粉丝有哪些*/
    public List<Special> getFans(Special special){
        return (List<Special>)jdbcTemplate.query("SELECT * FROM special WHERE spec_id=?",
                new Object[]{special.getU_id()},new BeanPropertyRowMapper<>(Special.class));//此时的getU_id其实是数据库的spec_id，但是用户一直之有自己的u_id
    }
}
