package com.example.technology_forum.controller;

import com.example.technology_forum.model.*;
import com.example.technology_forum.service.collectService;
import com.example.technology_forum.service.mailService;
import com.example.technology_forum.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class userController {

    private static final String USER_SESSION="USER_SESSION";

    @Resource(name = "userJDBCService")
    userService userRestService;

    @Resource(name="mailServiceImpl")
    mailService mailService;

    @Resource(name = "collectJDBCService")
    collectService collectService;

    @Resource
    RedisUtil redisUtil;



    /*一个简单的测试*/
    @RequestMapping("/test")
    public User test() {
        User user = new User();
        user.setU_id(1);
        user.setName("霸王龙");
        user.setPassword("123456");
        user.setSex("0");
        user.setEmail("513512942@qq.com");
        user.setInformation("hello world");
        log.info("测试一下：" + user);
        return user;
    }

    /*
     * 生成验证码
     */
    public String setCode(){
        return new VerifyCode().getCode();
    }

    /*
     * 检查用于名是否存在
     * @Param name password
     * */
    @PostMapping("/checkName")
    public AjaxResponse checkName(@RequestBody User user) {
        log.info("insertUser:{}", user);
        List<User> list = userRestService.findName(user);
        /*先看是否注册用户,用户名不能重复*/
        if (list.size() != 0) {
            return AjaxResponse.fail("该用户名已存在");
        }
        else{
            return AjaxResponse.success(true);
        }
    }


    /*
     * 发送邮箱验证码
     * @Param email
     * */
    @PostMapping("/sendMail")
    public @ResponseBody
    AjaxResponse sendMail(@RequestBody User user, HttpSession session){
        String code = this.setCode();
        session.setAttribute(user.getEmail(),code);//设置session
        session.setMaxInactiveInterval(60*5);//两分钟的时限
        redisUtil.set(user.getEmail() ,code, 60*5);//json序列化并存入redis数据库中
        log.info("sendMail{sessionCode}："+session.getAttribute(user.getEmail()));
        boolean flag=mailService.sendMail(user.getEmail(),"验证码", (String) session.getAttribute(user.getEmail()));
        if(flag){
            return AjaxResponse.success(true);
        }
        else{
            return AjaxResponse.fail("发送邮箱验证码失败！");
        }
    }


    /*
     * 发送邮箱——并在之前验证邮箱是否唯一
     * @Param email
     * */
    @PostMapping("/sendNewMail")
    public @ResponseBody AjaxResponse sendNewMail(@RequestBody User user,HttpSession session){
        User user1 = userRestService.findByEmail(user);
        /*先看是否注册用户*/
        if (user1!=null) {
            return AjaxResponse.fail("该邮箱已注册过用户！");
        }
        else{
            String code = this.setCode();
            session.setAttribute(user.getEmail(),code);//设置session
            session.setMaxInactiveInterval(60*5);//两分钟的时限
            redisUtil.set(user.getEmail() ,code, 60*5);//json序列化并存入redis数据库中
            log.info("sendMail{sessionCode}："+session.getAttribute(user.getEmail()));
            boolean flag=mailService.sendMail(user.getEmail(),"验证码", (String) session.getAttribute(user.getEmail()));
            if(flag){
                return AjaxResponse.success(true);
            }
            else{
                return AjaxResponse.fail("发送邮箱验证码失败！");
            }
        }
    }

    /*
     * 检查验证码
     * @Param verifyCode email
     * */
    @PostMapping("/checkCode")
    public @ResponseBody AjaxResponse checkCode(@RequestBody User user,HttpSession session){
        try {
            log.info("checkCode{sessionCode and user.getCode() and redisCode}"+session.getAttribute(user.getEmail())+"     "+user.getVerifyCode()+"   "+redisUtil.get(user.getEmail()));
            if (session.getAttribute(user.getEmail()).equals(user.getVerifyCode())) {
                return AjaxResponse.success(true);
            } else {
                return AjaxResponse.fail("验证失败！");
            }
        }catch (NullPointerException e){
            return AjaxResponse.fail("请先获取验证码！");
        }
    }


    /*
     * 注册
     * @Param name  password  email  verifyCode
     * */
    @PostMapping("/register")
    public @ResponseBody AjaxResponse register(@RequestBody User user,HttpSession session){
        log.info("register{sessionCode and user.getCode() and redisCode}"+session.getAttribute(user.getEmail())+"     "+user.getVerifyCode()+"   "+redisUtil.get(user.getEmail()));
        try {
            if (session.getAttribute(user.getEmail()).equals(user.getVerifyCode())) {
                userRestService.register(user);
                /*注册时创建默认收藏夹*/
                Favorites MyFavorites = new Favorites();
                MyFavorites.setFavorites_name("我的收藏夹");
                MyFavorites.setU_id(userRestService.getId(user));
                collectService.createFavorites(MyFavorites);
                return AjaxResponse.success(true);
            } else {
                return AjaxResponse.fail("验证失败！");
            }
        }catch (NullPointerException e){
            return AjaxResponse.fail("请先获取验证码！");
        }
    }




    /*
     * 登陆，返回用户的id
     * @Param name   password
     * */
    @PostMapping(value = "/login")
    public @ResponseBody AjaxResponse login(@RequestBody User user, HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        System.out.println(user.getName()+"       "+user.getPassword());
        System.out.println(request.getRequestURI()+"                  "+request.getRequestURL());
        String token = userRestService.login(response,user);   //登陆且获得一个token值
        if(token!=null){
            session.setAttribute(USER_SESSION,token); //设置session，将token存进去
            System.out.println("login{token}:"+token);
            session.setMaxInactiveInterval(60*60*24);//设置session的有效期为一天
            System.out.print("登录成功了");
            return AjaxResponse.success(userRestService.getId(user));//返回用户的id
        }else{
            System.out.print("账号或密码错误");
            return AjaxResponse.fail("账号或密码错误");
        }
    }



    /*用户登出，删除session*/
    @PostMapping("/logout")
    public AjaxResponse invalidate(HttpSession session){
        session.invalidate();
        return AjaxResponse.success(true);
    }

    /*
     * 修改密码
     * @Param  password u_id
     * */
    @PostMapping("/updatePassword")
    public @ResponseBody AjaxResponse updatePassword(@RequestBody User user){
        String name;
        if(user.getU_id()!=null) {
            name = userRestService.getName(user);
            if(name==null)  return AjaxResponse.fail("没有此用户");
            log.info("{userController_updatePassword_name1}:"+name);
        }else{
            User user1 = userRestService.getUserByEmail(user);
            name = user1.getName();
            log.info("{userController_updatePassword_name2}:"+name);
        }
        userRestService.updatePassword(user,name);
        return AjaxResponse.success(true);
    }

    /*
     * 先验证，再修改邮箱
     * @Param  email  verifyCode u_id
     * */
    @PostMapping("/updateEmail")
    public @ResponseBody AjaxResponse updateEmail(@RequestBody User user,HttpSession session){
        log.info("updateEmail{sessionCode and user.getCode() and redisCode}"+session.getAttribute(user.getEmail())+"     "+user.getVerifyCode()+"   "+redisUtil.get(user.getEmail()));
        try {
            log.info(session.getAttribute(user.getEmail())+"     "+user.getVerifyCode());
            if (session.getAttribute(user.getEmail()).equals(user.getVerifyCode())) {
                String name = userRestService.getName(user);
                userRestService.updateEmail(user,name);
                return AjaxResponse.success(true);
            } else {
                return AjaxResponse.fail("验证失败！");
            }
        }catch (NullPointerException e){
            return AjaxResponse.fail("请先获取验证码！");
        }
    }

    /*
     * 修改用户的基本信息
     * @Param u_id name sex information
     * */
    @PostMapping("/updateUserInfo")
    public @ResponseBody AjaxResponse updateUserInfo(@RequestBody User user){
        if (user.getName()!=null){
            userRestService.updateName(user);
        }
        if(user.getSex()!=null){
            userRestService.updateSex(user);
        }
        if(user.getInformation()!=null){
            userRestService.updateInformation(user);
        }
        if(user.getBirth()!=null){//Date的传参是什么？
            userRestService.updateBirth(user);
        }
        if(user.getPortrait()!=null){
            userRestService.updatePortrait(user);
        }
        userRestService.updateGrade(user);
        userRestService.updateLevel(user);
        return AjaxResponse.success(true);
    }

    /*
     * 获取用户的基本信息
     * @Param u_id
     * */
    @PostMapping("/getUserInfo")
    public AjaxResponse getUserInfo(@RequestBody User user){
        userRestService.updateGrade(user);
        userRestService.updateLevel(user);//获取前先更新等级

        Map<String,Object> userInfo = new HashMap<>();
        User user1 = userRestService.getUser(user);
        userInfo.put("u_id",user1.getU_id());
        userInfo.put("name",user1.getName());
        userInfo.put("email",user1.getEmail());
        userInfo.put("sex",user1.getSex());
        userInfo.put("information",user1.getInformation());
        userInfo.put("level",user1.getLevel());
        userInfo.put("birth",user1.getBirth());
        userInfo.put("portrait",user1.getPortrait());
        userInfo.put("grade",user1.getGrade());
        return AjaxResponse.success(userInfo);
    }


    /*
    * 排行榜
    * */
    @PostMapping("/sortUserByGrade")
    public @ResponseBody AjaxResponse sortUserByGrade(){
        List<User> userList = userRestService.sortUserByGrade();
        List<Map<String,Object>> result_list = new ArrayList<>();
        int i=1;
        for(User user:userList){
            Map<String,Object> userInfo = new HashMap<>();
            userInfo.put("id",user.getU_id());
            userInfo.put("rank",i);
            userInfo.put("name",user.getName());
            userInfo.put("portrait",user.getPortrait());
            userInfo.put("grade",user.getGrade());
            result_list.add(userInfo);
            i++;
        }
        return AjaxResponse.success(result_list);
    }

}
