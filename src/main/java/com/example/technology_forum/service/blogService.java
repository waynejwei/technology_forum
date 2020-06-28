package com.example.technology_forum.service;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Blog;

import java.util.List;

public interface blogService {

    /****************************************添加************************************************/
    void addBlog(Blog blog);

    /****************************************删除************************************************/
    void deleteBlog(Blog blog);

    /****************************************修改************************************************/
    AjaxResponse changeLike(Blog blog);

    void updateBlog(Blog blog);

    /****************************************查找(具体博客、排序)************************************************/
    int getBlogId(Blog blog);

    List<Blog> sortBlogByTime(Blog blog);

    List<Blog> sortBlogByLike(Blog blog);

    List<Blog> getAllBlog(Blog blog);

    Blog getBlogDetail(Blog blog);

    List<Blog> findBlogName(Blog blog);

    Blog findOnlyBlogName(Blog blog);
}
