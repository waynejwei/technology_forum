package com.example.technology_forum.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.apache.tomcat.util.buf.MessageBytes;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60*60*24)
public class sessionInterceptor implements HandlerInterceptor {

    private static final String USER_SESSION="USER_SESSION";

    /*进入所有网页前拦截*/
    @Override
    public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
        System.out.println("开始请求地址拦截");
        //首页路径以及登录放行

        /***********获取当前的uri**************/
        Object a = findCoyoteRequest(arg0);
        Field coyoteRequest = a.getClass().getDeclaredField("coyoteRequest");
        coyoteRequest.setAccessible(true);
        Object b = coyoteRequest.get(a);

        Field uriMB = b.getClass().getDeclaredField("uriMB");
        uriMB.setAccessible(true);
        MessageBytes c = (MessageBytes)uriMB.get(b);
        System.out.println(c.getString());
        /*************************/

        if ("/api/login".equals(c.getString()) || "/api/updatePassword".equals(c.getString())) {
            System.out.println("login界面不用拦截");
            return true;
        }
        
        if("/api/register".equals(c.getString()) || "/api/sendMail".equals(c.getString()) || "/api/checkName".equals(c.getString()) || "/api/sendNewMail".equals(c.getString()) || "/api/checkCode".equals(c.getString())){
            System.out.println("register不用拦截");
            return true;
        }
        //重定向
        Object object = arg0.getSession().getAttribute(USER_SESSION);
        arg0.getSession().setMaxInactiveInterval(60*60*24);//获取session的最大有效期为一天
        if(object!=null) System.out.println("object is:"+object);
        if (null == object) {
            System.out.println("自动登陆失败！");
//            arg1.sendRedirect("/api/login");
            returnJSONErr(arg1);
            return false;
        }
        System.out.println("自动登陆成功！");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("返回视图或String之前的处理");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("返回视图或String之后的处理");
    }

    /*返回登陆超时的信息*/
    public void returnJSONErr(HttpServletResponse response){
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            writer = response.getWriter();
            JSONObject ajaxResponse = new JSONObject();
            ajaxResponse.put("code",409);
            ajaxResponse.put("isOk",false);
            ajaxResponse.put("message","登陆超时，请重新登陆");
            writer.print(ajaxResponse.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
    }

    private Class getClassByName(Class classObject, String name){
        Map<Class, List<Field>> fieldMap = new HashMap<>();
        Class returnClass = null;
        Class tempClass = classObject;
        while (tempClass != null) {
            fieldMap.put(tempClass, Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }

        for(Map.Entry<Class,List<Field>> entry: fieldMap.entrySet()){
            for (Field f : entry.getValue()) {
                if(f.getName().equals(name)){
                    returnClass = entry.getKey();
                    break;
                }
            }
        }
        return returnClass;
    }

    private Object findCoyoteRequest(Object request)  throws Exception {
        Class a = getClassByName(request.getClass(), "request");
        Field request1 = a.getDeclaredField("request");
        request1.setAccessible(true);
        Object b = request1.get(request);
        if (getClassByName(b.getClass(), "coyoteRequest") == null) {
            return findCoyoteRequest(b);
        } else {
            return b;
        }
    }


}

