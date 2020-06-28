package com.example.technology_forum.service;

import com.alibaba.fastjson.JSON;
import com.example.technology_forum.dao.*;
import com.example.technology_forum.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class userJDBCService implements userService {

    public static final String COOKIE_NAME_TOKEN = "token";//cookie名字
    public static final int TOKEN_EXPIRE = 60*60*24;//限制时间为一天

    @Resource
    userDao userDao;

    @Resource
    blogDao blogDao;

    @Resource
    commentDao commentDao;

    @Resource
    questionDao questionDao;

    @Resource
    answerDao answerDao;


    @Resource
    private RedisUtil redisUtil;

    /****************************************登陆注册************************************************/
    @Override
    public String login(HttpServletResponse response, User user) {
        //        return userJDBCDao.login(name,password);
        String token=null;
//        List<User> list = this.findName(user);
        List<User> list = userDao.findByName(user);
        /*先看是否注册用户*/
        if (list.size() == 0) {
            log.info("没有注册此用户!");
        }
        else {
            int flag=0;//判断是否登陆成功
            for (User u : list) {
                if (user.getPassword() == null) log.info("用户没有输密码");
                if (user.getPassword().equals(u.getPassword())) {
                    flag = 1;
                    break;
                }
            }
            if(flag==0){
                log.info("密码错误！");
            }
            else{
                log.info("登陆成功！");
                User user1 = new User();
                user1.setName(user.getName());
                user1.setPassword(user.getPassword());
                token = UUID.randomUUID().toString().replace("-","");
                addCookie(response,token,user1);
            }
        }
        return token;
    }

    @Override
    public void register(User user) {
        userDao.register(user);
    }

    /****************************************获取用户信息************************************************/
    @Override
    public List<User> findName(User user) {
        return userDao.findByName(user);
    }

    @Override
    public User findByEmail(User user) {
        try{
            return userDao.getUserByEmail(user);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public int getId(User user) {
        User user1 = userDao.getId(user);
        return user1.getU_id();
    }

    @Override
    public String getName(User user) {
        try {
            User user1 = userDao.getUserInfo(user);//通过id获取用户
            return user1.getName();
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public User getUser(User user) {
        return userDao.getUserInfo(user);
    }

    //根据token获取user
    @Override
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = JSON.parseObject(redisUtil.get(COOKIE_NAME_TOKEN + "::" + token), User.class);
        //重置有效期
        if (user == null) {
//            throw new GlobalException(CodeMsg.USER_NOT_LOGIN);
            return null;
        }
        addCookie(response, token, user);
        return user;
    }

    @Override
    public User getUserByEmail(User user) {
        return userDao.getUserByEmail(user);
    }


    /*计算用户分数——根据获赞总数和评论总数之和*/
    @Override
    public int getUserGrade(User user) {
        /*博客数量、获赞数*/
        Blog blog = new Blog();
        blog.setU_id(user.getU_id());
        List<Blog> blog_list = blogDao.sortBlogByLike(blog);//根据id获取用户信息
        int blog_like = 0;//博客点赞数
        for(Blog blog1:blog_list){
            if(blog1.getLike_num()==0)  break;  //因为是按照点赞数降序排列的
            blog_like += blog1.getLike_num();
        }
        int blog_num = blog_list.size();
        log.info("{userService-getUserLevel-blog_like}:"+blog_like);

        /*用户评论个数、获赞数*/
        Comment comment = new Comment();
        comment.setU_id(user.getU_id());

        List<Comment> comment_list = commentDao.getUserComment(comment);
        int comment_like = 0;
        for(Comment comment1:comment_list){
            if(comment1.getLike_num()==0)  break;
            comment_like += comment1.getLike_num();
        }
        int comment_num = comment_list.size();
        log.info("{userService-getUserLevel-comment_num}:"+comment_num);
        log.info("{userService-getUserLevel-comment_like}:"+comment_like);

        /*帖子数量、获赞数*/
        Question question = new Question();
        question.setU_id(user.getU_id());
        List<Question> question_list = questionDao.sortLikeTimeDESC(question);
        int question_like = 0;
        for(Question question1:question_list){
            if(question1.getLike_num()==0)  break;
            question_like += question1.getLike_num();
        }
        int question_num = question_list.size();
        log.info("{userService-getUserLevel-question_like}:"+question_like);

        /*用户回复个数、获赞数*/
        Answer answer = new Answer();
        answer.setU_id(user.getU_id());

        List<Answer> answer_list = answerDao.getUserAnswer(answer);
        int answer_like = 0;
        for(Answer answer1:answer_list){
            if(answer1.getLike_num()==0)  break;
            answer_like += answer1.getLike_num();
        }
        int answer_num = answer_list.size();
        log.info("{userService-getUserLevel-answer_num}:"+answer_num);
        log.info("{userService-getUserLevel-answer_like}:"+answer_like);

        int grade = blog_like+blog_num+comment_like+comment_num+question_like+question_num+answer_like+answer_num;
        log.info("{userService-getUserLevel-grade}:"+grade);

        return grade;
    }

    @Override
    public List<User> sortUserByGrade() {
        return userDao.sortUserByGrade();
    }


    /*根据User添加cookie值*/
    @Override
    public void addCookie(HttpServletResponse response, String token, User user) {
        //将token存入到redis
        redisUtil.set(COOKIE_NAME_TOKEN + "::" + token, JSON.toJSONString(user), TOKEN_EXPIRE);
        //将token写入cookie
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(TOKEN_EXPIRE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /****************************************修改用户信息************************************************/
    @Override
    public void updatePassword(User user, String name) {
        userDao.updatePassword(user,name);
    }

    @Override
    public void updateEmail(User user, String name) {
        userDao.updateEmail(user,name);
    }

    @Override
    public void updateName(User user) {
        userDao.updateName(user);
    }

    @Override
    public void updateSex(User user) {
        userDao.updateSex(user);
    }

    @Override
    public void updateInformation(User user) {
        userDao.updateInformation(user);
    }

    @Override
    public void updateBirth(User user) {
        userDao.updateBirth(user);
    }

    /*每隔10分，就上一个等级*/
    @Override
    public void updateLevel(User user) {
        int grade = getUserGrade(user);
        int level=1;
        while(grade>10){
            level++;
            grade=grade-10;
        }
        user.setLevel(level);
        userDao.updateLevel(user);
    }

    @Override
    public void updatePortrait(User user) {
        userDao.updatePortrait(user);
    }

    @Override
    public void updateGrade(User user) {
        user.setGrade(getUserGrade(user));
        userDao.updateGrade(user);
    }




}
