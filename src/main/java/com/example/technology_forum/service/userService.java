package com.example.technology_forum.service;

import com.example.technology_forum.model.User;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface userService {

    /****************************************登陆************************************************/
    String login(HttpServletResponse response, User user);

    /****************************************注册************************************************/
    void register(User user);

    /****************************************查找信息************************************************/
    List<User> findName(User user);

    User findByEmail(User user);

    int getId(User user);

    String getName(User user);

    User getUser(User user);

    User getByToken(HttpServletResponse response, String token);//获取token值

    User getUserByEmail(User user);

    void addCookie(HttpServletResponse response, String token, User user);//添加cookie值

    int getUserGrade(User user);

    List<User> sortUserByGrade();

    /****************************************修改用户信息************************************************/
    void updatePassword(User user,String name);

    void updateEmail(User user,String name);

    void updateName(User user);

    void updateSex(User user);

    void updateInformation(User user);

    void updateBirth(User user);

    void updateLevel(User user);

    void updatePortrait(User user);

    void updateGrade(User user);

}
