package com.example.technology_forum.controller;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Blog;
import com.example.technology_forum.model.User;
import com.example.technology_forum.service.blogService;
import com.example.technology_forum.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
public class blogController {


    @Resource(name = "blogJDBCService")
    blogService blogService;

    @Resource(name = "userJDBCService")
    userService userService;


    /*
     * 添加博客
     * @Param name  content html  tag u_id
     * */
    @PostMapping("/addBlog")
    public @ResponseBody
    AjaxResponse addBlog(@RequestBody Blog blog){
        Blog result_blog = blogService.findOnlyBlogName(blog);
        if(result_blog!=null){
            log.info("{blogService-addBlog-result_blog_id}:"+result_blog.getBlog_id());
            return AjaxResponse.fail("博客名重复！");
        }
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date();
        log.info("addBlog{date}:"+date);
        blog.setUpload_time(date);
        blogService.addBlog(blog);
        int blog_id = blogService.getBlogId(blog);
        if(blog_id==0)  return AjaxResponse.fail("添加失败！");
        return AjaxResponse.success(blog_id);
    }


    /*
     * 删除博客
     * @Param blog_id（session获取blog_id）
     * */
    @PostMapping("/deleteBlog")
    public @ResponseBody AjaxResponse deleteBlog(@RequestBody Blog blog){
        blogService.deleteBlog(blog);
        return AjaxResponse.success(true);
    }


    /*
     * 修改博客名字和内容
     * @Param  name content html tag blog_id
     * */
    @PostMapping("/updateBlog")
    public @ResponseBody AjaxResponse updateBlog(@RequestBody Blog blog){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date();
        blog.setUpload_time(date);
        blogService.updateBlog(blog);
        return AjaxResponse.success(true);
    }


    /*
     * 改变博客点赞数
     * @Param blog_id  u_id  typeOfLike
     * */
    @PostMapping("changeLike")
    public @ResponseBody AjaxResponse changeLike(@RequestBody Blog blog){
        return blogService.changeLike(blog);
    }


    /*返回简写的博客信息*/
    public Map<Object, Object> getsortBlogInfo(Blog blog){
        Map<Object, Object> map = new HashMap<>();
        map.put("blog_id",blog.getBlog_id());
        map.put("name",blog.getName());
        map.put("tag",blog.getTag());
        map.put("author_name",blog.getAuthor_name());
        map.put("like_num",blog.getLike_num());
        map.put("upload_time",blog.getUpload_time());
        return map;
    }


    /*
     * 通过博客名查询博客
     * @Param name
     * */
    @PostMapping("/findBlogName")
    public @ResponseBody AjaxResponse findBlogName(@RequestBody Blog blog){
        List<Blog> list =  blogService.findBlogName(blog);//通过此博客名字，获取此博客
        return getAjaxResponse(list);
    }

    private AjaxResponse getAjaxResponse(List<Blog> list) {
        List<Map<Object, Object>> result_list = new ArrayList<>();
        for (Blog blog1 : list) {
            /*获取作者的名字*/
            int author_id = blog1.getU_id();//获取作者的u_id
            User user = new User();
            user.setU_id(author_id);
            String author_name = userService.getName(user);
            blog1.setAuthor_name(author_name);

            Map<Object, Object> map = getsortBlogInfo(blog1);
            result_list.add(map);
        }
        return AjaxResponse.success(result_list);
    }


    /*
     * 通过时间和点赞数来查询,个人博客
     * @Param tag(要不要限制数量？) u_id(作者的id)
     * @Return author_id
     * */
    @PostMapping("/sortBlogByLike")
    public @ResponseBody AjaxResponse sortBlogByLike(@RequestBody Blog blog){
        List<Blog> list = blogService.sortBlogByLike(blog);   //作者所写的博客
        return getAjaxResponse(list);
    }


    /*
     * 通过时间来查询，个人博客
     * @Param tag typeOfTime(要不要限制数量？) u_id
     * */
    @PostMapping("/sortBlogByTime")
    public @ResponseBody AjaxResponse sortBlogByTime(@RequestBody Blog blog){
        List<Blog> list = blogService.sortBlogByTime(blog);
        return getAjaxResponse(list);
    }

    /*
     * 按照分类获取所有博客[按照点赞数排序]
     * @Param u_id  tag
     * */
    @PostMapping("/getAllBlog")
    public @ResponseBody AjaxResponse getAllBlog(@RequestBody Blog blog){
        List<Blog> list = blogService.getAllBlog(blog);
        List<Map<Object, Object>> result_list = new ArrayList<>();
        if(list.size()!=0){
            //每一个博客
            for (Blog result_blog : list) {
                /*获取作者的名字*/
                int author_id = result_blog.getU_id();//获取作者的u_id
                User user = new User();
                user.setU_id(author_id);
                String author_name = userService.getName(user);
                result_blog.setAuthor_name(author_name);

                Map<Object, Object> map = getsortBlogInfo(result_blog);
                result_list.add(map);
            }
        }
        return AjaxResponse.success(result_list);
    }

    /*
     * 通过博客id返回博客的所有信息
     * @Param blog_id  u_id(便于查看该用户是否点赞)
     * */
    @PostMapping("/getBlogDetail")
    public @ResponseBody AjaxResponse getBlogDetail(@RequestBody Blog blog){
        Blog blog1 = blogService.getBlogDetail(blog);//根据博客id获取博客信息
        if(blog1==null)  return AjaxResponse.fail("没有此博客");
        /*获取作者的名字*/
        int author_id = blog1.getU_id();//获取作者的u_id
        User user = new User();
        user.setU_id(author_id);
        String author_name = userService.getName(user);
        blog1.setAuthor_name(author_name);

        /*查找此人是否点赞*/
        String likePeople = blog1.getLikePeople();//点赞人的字符串
        if(likePeople==null || likePeople.equals("")){//还无人点赞
            blog1.setIs_like("false");
        }
        else {
            String[] peoples = likePeople.split(",");//通过，分割
            boolean flag = false;
            for (String people : peoples) {//查找里面是否有
                if (people.equals(String.valueOf(blog.getU_id()))) {
                    blog1.setIs_like("true");//该用户点赞
                    flag = true;
                }
            }
            if (!flag) blog1.setIs_like("false");
        }

        Map<Object, Object> map = getsortBlogInfo(blog1);
        map.put("author_id",blog1.getU_id());
        map.put("content",blog1.getContent());
        map.put("html",blog1.getHtml());
        map.put("is_like",blog1.getIs_like());

        return AjaxResponse.success(map);
    }
}
