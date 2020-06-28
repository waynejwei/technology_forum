package com.example.technology_forum.dao;

import com.example.technology_forum.model.Comment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class commentDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /****************************************增************************************************/
    /*给博客写评论写评论*/
    public void writeComment(Comment comment){
        jdbcTemplate.update("INSERT INTO comment(blog_id,author_id,u_id,content,comment_time,depth,story_master,is_master,room) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)",
                comment.getBlog_id(),
                comment.getAuthor_id(),
                comment.getU_id(),
                comment.getContent(),
                comment.getComment_time(),
                comment.getDepth(),
                comment.getStory_master(),
                comment.getIs_master(),
                comment.getRoom());
    }

    /****************************************删************************************************/
    /*通过id删除评论*/
    public void deleteComment(Comment comment){
        jdbcTemplate.update("DELETE FROM comment WHERE comment_id=?",comment.getComment_id());
    }


    /****************************************改************************************************/
    /*给评论点赞*/
    public void updateCommentLikeNum(Comment comment,int addNum){
        jdbcTemplate.update("UPDATE comment SET like_num=like_num+? WHERE comment_id=?",addNum,comment.getComment_id());
    }

    /*点赞后加入到likePeople——第一次*/
    public void addFirstLikePeople(Comment comment){
        jdbcTemplate.update("UPDATE comment SET like_people=? WHERE comment_id=?",comment.getU_id(),comment.getComment_id());
        jdbcTemplate.update("UPDATE comment SET like_people=CONCAT(like_people,',') WHERE comment_id=?",comment.getComment_id());
    }

    /*点赞后加入打到likePeople*/
    public void addLikePeople(Comment comment){
        jdbcTemplate.update("UPDATE comment SET like_people=CONCAT(like_people,?) WHERE comment_id=?",
                comment.getU_id(),comment.getComment_id());
        jdbcTemplate.update("UPDATE comment SET like_people=CONCAT(like_people,',') WHERE comment_id=?",comment.getComment_id());
    }

    /*取消点赞*/
    public void reduceLikePeople(int comment_id,String u_id){
        jdbcTemplate.update("UPDATE comment SET like_people=REPLACE(like_people,?,'') WHERE comment_id=?",u_id,comment_id);
    }

    /****************************************查************************************************/
    /*根据博客id查看层主评论信息(点赞降序)*/
    public List<Comment> getMasterCommentInfo(Comment comment){
        return (List<Comment>)jdbcTemplate.query("SELECT * FROM comment WHERE blog_id=? AND is_master=true ORDER BY like_num DESC,comment_time DESC",
                new Object[]{comment.getBlog_id()},new BeanPropertyRowMapper<>(Comment.class));
    }

    /*根据博客id和层数来获取一般评论的信息(时间降序)*/
    public List<Comment> getCommonCommentInfo(Comment comment){
        return (List<Comment>)jdbcTemplate.query("SELECT * FROM comment WHERE blog_id=? AND is_master=false AND depth=? ORDER BY comment_time DESC",
                new Object[]{comment.getBlog_id(),comment.getDepth()},new BeanPropertyRowMapper<>(Comment.class));
    }

    /*通过comment_id来查看评)*/
    public Comment getCommentById(Comment comment){
        return (Comment)jdbcTemplate.queryForObject("SELECT * FROM comment WHERE comment_id=?",
                new Object[]{comment.getComment_id()},new BeanPropertyRowMapper<>(Comment.class));
    }

    /*根据blog_id,depth,room获取评论*/
    public Comment getCommentByOtherId(Comment comment){
        return (Comment)jdbcTemplate.queryForObject("SELECT * FROM comment WHERE blog_id=? and depth=? and room=?",
                new Object[]{comment.getBlog_id(),comment.getDepth(),comment.getRoom()},new BeanPropertyRowMapper<>(Comment.class));
    }

    /*查找已有评论的层数*/
    public Integer getDepth(Comment comment){
        return (Integer)jdbcTemplate.queryForObject("SELECT MAX(depth) FROM comment WHERE blog_id=? GROUP BY blog_id",
                new Object[]{comment.getBlog_id()},Integer.class);
    }

    /*查找当前层数的已有的房间号*/
    public Integer getRoom(Comment comment){
        return (Integer)jdbcTemplate.queryForObject("SELECT MAX(room) FROM comment WHERE blog_id=? and depth=?",
                new Object[]{comment.getBlog_id(),comment.getDepth()},Integer.class);
    }

    /*查看某个用户所发的评论点赞数降序*/
    public Integer getUserCommentNum(Comment comment){
        return (Integer) jdbcTemplate.queryForObject("SELECT COUNT(comment_id) FROM comment WHERE u_id=?",
                new Object[]{comment.getU_id()},Integer.class);
    }

    /*查看用户所有评论消息*/
    public List<Comment> getUserComment(Comment comment){
        return (List<Comment>)jdbcTemplate.query("SELECT * FROM comment WHERE u_id=? ORDER BY like_num DESC,comment_time DESC",
                new Object[]{comment.getU_id()},new BeanPropertyRowMapper<>(Comment.class));
    }


}
