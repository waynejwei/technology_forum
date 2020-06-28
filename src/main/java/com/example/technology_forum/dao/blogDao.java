package com.example.technology_forum.dao;

import com.example.technology_forum.model.Blog;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class blogDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /****************************************添加************************************************/
    /*增加博客*/
    public void addBlog(Blog blog){
        jdbcTemplate.update("INSERT INTO blog(u_id,name,content,html,tag,upload_time) values(?,?,?,?,?,?)",
                blog.getU_id(),blog.getName(),blog.getContent(),blog.getHtml(),blog.getTag(),blog.getUpload_time());
    }


    /****************************************删除************************************************/
    /*根据id删除博客*/
    public void deleteBlog(Blog blog){
        jdbcTemplate.update("DELETE from blog where blog_id=?",blog.getBlog_id());
    }



    /****************************************修改************************************************/
    /*根据id修改点赞次数*/
    public void updateBlogLikeNum(Blog blog,int addNum){
        jdbcTemplate.update("UPDATE blog SET like_num=like_num+? WHERE Blog_id=?",addNum,blog.getBlog_id());
    }


    /*根据id修改博客*/
    public void updateBlog(Blog blog){
        jdbcTemplate.update("UPDATE blog SET name=?,content=?,html=?,tag=?,upload_time=? WHERE blog_id=?",blog.getName(),blog.getContent(),blog.getHtml(),blog.getTag(),blog.getUpload_time(),blog.getBlog_id());
    }


    /****************************************排序查询查询************************************************/
    /*根据博客类型查询——时间降序  个人博客*/
    public List<Blog> sortBlogTagTimeDesc(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE tag=? and u_id=? "+
                "ORDER BY upload_time DESC",new Object[]{blog.getTag(),blog.getU_id()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*根据博客类型查询——时间升序  个人博客*/
    public List<Blog> sortBlogTagTime(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE tag=? and u_id=? " +
                "ORDER BY upload_time",new Object[]{blog.getTag(),blog.getU_id()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*根据博客类型查询——时间点赞数降序 个人博客*/
    public List<Blog> sortBlogTagLike(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE tag=? and u_id=? " +
                "ORDER BY like_num DESC,upload_time DESC",new Object[]{blog.getTag(),blog.getU_id()},new BeanPropertyRowMapper<>(Blog.class));
    }


    /*根据上传时间查询——降序 个人博客*/
    public List<Blog> sortBlogTimeDesc(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE u_id=? " +
                "ORDER BY upload_time DESC",new Object[]{blog.getU_id()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*根据上传时间查询——升序 个人博客*/
    public List<Blog> sortBlogTime(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE u_id=? " +
                "ORDER BY upload_time",new Object[]{blog.getU_id()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*根据点赞数和时间查询——降序 个人博客*/
    public List<Blog> sortBlogByLike(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE u_id=? " +
                "ORDER BY like_num DESC,upload_time DESC",new Object[]{blog.getU_id()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*按照分类获取所有博客[按照点赞数排序]*/
    public List<Blog> sortAllBlogTagLike(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog WHERE tag=? " +
                "ORDER BY like_num DESC,upload_time DESC",new Object[]{blog.getTag()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*获取所有博客[按照点赞数排序]*/
    public List<Blog> sortAllBlogLike(Blog blog){
        return (List<Blog>)jdbcTemplate.query("SELECT *FROM blog " +
                "ORDER BY like_num DESC,upload_time DESC",new BeanPropertyRowMapper<>(Blog.class));
    }

    /****************************************具体博客查询************************************************/
    /*根据博客名模糊查询博客信息*/
    public List<Blog> findBlogName(Blog blog){
        return (List<Blog>) jdbcTemplate.query("SELECT * FROM blog WHERE name like CONCAT(CONCAT('%',?,'%'))",
                new Object[]{blog.getName()},new BeanPropertyRowMapper<>(Blog.class));
    }


    /*根据博客名唯一查询博客信息*/
    public Blog findOnlyBlogName(Blog blog){
        return (Blog) jdbcTemplate.queryForObject("SELECT * FROM blog WHERE name=?",
                new Object[]{blog.getName()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /*通过博客id查找博客信息*/
    public Blog getBlogDetail(Blog blog){
        return (Blog)jdbcTemplate.queryForObject("SELECT *FROM blog WHERE blog_id=?",
                new Object[]{blog.getBlog_id()},new BeanPropertyRowMapper<>(Blog.class));
    }

    /****************************************修改like_people信息************************************************/
    /*向likePeople中添加人*/
    public void addLikePeople(int blog_id,String u_id){
        jdbcTemplate.update("UPDATE blog SET like_people=CONCAT(like_people,?) WHERE blog_id=?",u_id,blog_id);
        jdbcTemplate.update("UPDATE blog SET like_people=CONCAT(like_people,',') WHERE blog_id=?",blog_id);
    }

    /*第一次添加*/
    public void addFirstLikePeople(int blog_id,String u_id){
        jdbcTemplate.update("UPDATE blog SET like_people=? WHERE blog_id=?",u_id,blog_id);
        jdbcTemplate.update("UPDATE blog SET like_people=CONCAT(like_people,',') WHERE blog_id=?",blog_id);
    }

    /*向likePeople中减少人*/
    public void reduceLikePeople(int blog_id,String u_id){
        jdbcTemplate.update("UPDATE blog SET like_people=REPLACE(like_people,?,'') WHERE blog_id=?",u_id,blog_id);
    }
}
