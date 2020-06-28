package com.example.technology_forum.service;

import com.example.technology_forum.dao.blogDao;
import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Blog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class blogJDBCService implements blogService {

    @Resource
    blogDao blogDao;

    /****************************************添加************************************************/
    @Override
    public void addBlog(Blog blog) {
        blogDao.addBlog(blog);
    }

    /****************************************删除************************************************/
    @Override
    public void deleteBlog(Blog blog) {
        blogDao.deleteBlog(blog);
    }

    /****************************************修改************************************************/
    /*修改博客的点赞数，包括点赞的人*/
    @Override
    public AjaxResponse changeLike(Blog blog) {
        try{
            Blog blog1 = blogDao.getBlogDetail(blog);//根据blog_id找博客
            String likePeople = blog1.getLikePeople();
            if(blog.getTypeOfLike().equals("add")) {//添加点赞数
                if(likePeople==null || likePeople.equals("")){//第一次添加，因为如果为null的话concat不能连接
                    blogDao.addFirstLikePeople(blog.getBlog_id(), String.valueOf(blog.getU_id()));
                }
                else{
                    String[] peoples = likePeople.split(",");//通过，分割
                    for (String people : peoples) {//查找里面是否有
                        if (people.equals(String.valueOf(blog.getU_id()))) {
                            return AjaxResponse.fail("该用户已点赞");
                        }
                    }
                    blogDao.addLikePeople(blog.getBlog_id(), String.valueOf(blog.getU_id()));
                }
                blogDao.updateBlogLikeNum(blog, 1);//点赞数+1
                return AjaxResponse.success(true);
            }
            else{//取消点赞
                if(likePeople==null || likePeople.equals(""))  return AjaxResponse.fail("该用户还未点赞");
                else{
                    String[] peoples = likePeople.split(",");//通过，分割
                    for (String people : peoples) {//查找里面是否有
                        if (people.equals(String.valueOf(blog.getU_id()))) {
                            blogDao.reduceLikePeople(blog.getBlog_id(), String.valueOf(blog.getU_id()) + ',');
                            blogDao.updateBlogLikeNum(blog, -1);//点赞数+1
                            return AjaxResponse.success(true);
                        }
                    }
                }
                return AjaxResponse.fail("该用户还未点赞");//如果前面的博客都没有找到它的名字，就表示没有点赞
            }
        }catch (EmptyResultDataAccessException e){
            return AjaxResponse.fail("没有此博客！");
        }
    }

    @Override
    public void updateBlog(Blog blog) {
        blogDao.updateBlog(blog);
    }


    /****************************************查找单个博客具体信息************************************************/
    @Override
    public int getBlogId(Blog blog) {
        try{
            Blog blog1 = blogDao.findOnlyBlogName(blog);//根据博客名唯一查询博客
            return blog1.getBlog_id();
        }catch(EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public Blog findOnlyBlogName(Blog blog) {
        try{
            return blogDao.findOnlyBlogName(blog);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public Blog getBlogDetail(Blog blog) {
        try{
            return blogDao.getBlogDetail(blog);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    /****************************************查找所有博客(排序/未排序)************************************************/
    @Override
    public List<Blog> sortBlogByTime(Blog blog) {
        log.info("sortByTime{hasTag}:"+blog.getTag());
        if(blog.getTag().equals("all")){//查找全部
            if(blog.getTypeOfTime().equals("new")) {
                return blogDao.sortBlogTimeDesc(blog);
            }
            else{
                return blogDao.sortBlogTime(blog);
            }
        }
        else{//有分类
            if(blog.getTypeOfTime().equals("new")){//降序
                return blogDao.sortBlogTagTimeDesc(blog);
            }
            else{
                return blogDao.sortBlogTagTime(blog);
            }
        }
    }

    @Override
    public List<Blog> sortBlogByLike(Blog blog) {
        if(blog.getTag().equals("all")){//查找全部的
            return blogDao.sortBlogByLike(blog);
        }
        else{
            return blogDao.sortBlogTagLike(blog);//有分类的
        }
    }

    @Override
    public List<Blog> getAllBlog(Blog blog) {
        if(blog.getTag().equals("all")){
            return blogDao.sortAllBlogLike(blog);
        }
        else{
            return blogDao.sortAllBlogTagLike(blog);
        }
    }


    @Override
    public List<Blog> findBlogName(Blog blog) {
        return blogDao.findBlogName(blog);
    }

}
