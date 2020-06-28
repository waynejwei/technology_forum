package com.example.technology_forum.dao;

import com.example.technology_forum.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class userDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /****************************************注册************************************************/
    /*注册用户,向数据库添加用户名、密码、邮箱*/
    public void register(User user){
        jdbcTemplate.update("INSERT INTO user(name,password,email)VALUES(?,?,?)",
                user.getName(),
                user.getPassword(),
                user.getEmail());
    }


    /****************************************登陆************************************************/
    /*登陆，根据用户名、密码检验用户*/
    public User login(User user){
        return (User) jdbcTemplate.queryForObject("SELECT * FROM  user WHERE name=? and password=?",
                new Object[]{user.getName(),user.getPassword()},new BeanPropertyRowMapper(User.class));
    }



    /****************************************修改信息************************************************/
    /*根据用户名修改密码*/
    public void updatePassword(User user,String name){
        jdbcTemplate.update("UPDATE user SET password=? WHERE name=?",user.getPassword(),name);
    }

    /*根据用户名修改邮箱*/
    public void updateEmail(User user,String name){
        jdbcTemplate.update("UPDATE user SET email=? WHERE name=?",user.getEmail(),name);
    }


    /*根据id修改用户名*/
    public void updateName(User user){
        jdbcTemplate.update("UPDATE user SET name=? WHERE u_id=?",user.getName(),user.getU_id());
    }

    /*根据id修改用户性别*/
    public void updateSex(User user){
        jdbcTemplate.update("UPDATE user SET sex=? WHERE u_id=?",user.getSex(),user.getU_id());
    }

    /*根据id修改用户个性签名*/
    public void updateInformation(User user){
        jdbcTemplate.update("UPDATE user SET information=? WHERE u_id=?",user.getInformation(),user.getU_id());
    }

    /*根据id修改用户的等级*/
    public void updateLevel(User user){
        jdbcTemplate.update("UPDATE user SET level=? WHERE u_id=?",user.getLevel(),user.getU_id());
    }

    /*根据id修改用户的生日*/
    public void updateBirth(User user){
        jdbcTemplate.update("UPDATE user SET birth=? WHERE u_id=?",user.getBirth(),user.getU_id());
    }

    /*根据id修改用户头像*/
    public void updatePortrait(User user){
        jdbcTemplate.update("UPDATE user SET portrait=? WHERE u_id=?",user.getPortrait(),user.getU_id());
    }

    /*改变用户分数*/
    public void updateGrade(User user){
        jdbcTemplate.update("UPDATE user SET grade=? WHERE u_id=?",user.getGrade(),user.getU_id());
    }



    /****************************************查找信息************************************************/
    /*根据用户名查找用户*/
    public List<User> findByName(User user){
        return (List<User>) jdbcTemplate.query("SELECT * FROM user WHERE name=?",
                new Object[]{user.getName()},new BeanPropertyRowMapper<>(User.class));
    }

    /*根据姓名获取用户的id*/
    public User getId(User user){
        return (User) jdbcTemplate.queryForObject("SELECT * FROM user WHERE name=?",
                new Object[]{user.getName()},new BeanPropertyRowMapper<>(User.class));
    }

    /*根据用户id获取用户*/
    public User getUserInfo(User user){
        return (User) jdbcTemplate.queryForObject("SELECT * FROM user WHERE u_id=?",
                new Object[]{user.getU_id()},new BeanPropertyRowMapper<>(User.class));
    }

    /*根据用户email获取用户*/
    public User getUserByEmail(User user){
        return (User) jdbcTemplate.queryForObject("SELECT * FROM user WHERE email=?",
                new Object[]{user.getEmail()},new BeanPropertyRowMapper<>(User.class));
    }


    /*排行榜——根据分数，只显示前50名*/
    public List<User> sortUserByGrade(){
        return (List<User>)jdbcTemplate.query("SELECT * FROM(SELECT * FROM user limit 50) user " +
                "ORDER BY grade DESC;",new BeanPropertyRowMapper<>(User.class));
    }

}
