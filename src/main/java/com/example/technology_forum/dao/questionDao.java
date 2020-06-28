package com.example.technology_forum.dao;

import com.example.technology_forum.model.Question;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class questionDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    /****************************************增加************************************************/
    /*写帖子*/
    public void addQuestion(Question question){
        jdbcTemplate.update("INSERT INTO question(u_id,content,tag,ask_time) VALUES(?,?,?,?)",
                question.getU_id(),
                question.getContent(),
                question.getTag(),
                question.getAsk_time());
    }

    /****************************************删除************************************************/
    /*删除帖子*/
    public void deleteQuestion(Question question){
        jdbcTemplate.update("DELETE FROM question WHERE question_id=?",question.getQuestion_id());
    }

    /****************************************修改************************************************/
    /*修改帖子内容*/
    public void updateContent(Question question){
        jdbcTemplate.update("UPDATE question SET content=?,ask_time=? WHERE question_id=?",
                question.getContent(),
                question.getAsk_time(),
                question.getQuestion_id());
    }

    /*修改帖子点赞数*/
    public void changeLike(Question question,int add){
        jdbcTemplate.update("UPDATE question SET like_num=like_num+? WHERE question_id=?",
                add,question.getQuestion_id());
    }

    /*第一次向like_people添加人*/
    public void updateFirstLikePeople(Question question){
        jdbcTemplate.update("UPDATE question SET like_people=? WHERE question_id=?",
                question.getU_id(),question.getQuestion_id());
        jdbcTemplate.update("UPDATE question SET like_people=CONCAT(like_people,',') WHERE question_id=?",
                question.getQuestion_id());
    }

    /*向like_people添加人*/
    public void updateLikePeople(Question question){
        jdbcTemplate.update("UPDATE question SET like_people=CONCAT(like_people,?) WHERE question_id=?",
                question.getU_id(),question.getQuestion_id());
        jdbcTemplate.update("UPDATE question SET like_people=CONCAT(like_people,',') WHERE question_id=?",
                question.getQuestion_id());
    }

    /*删除like_people里的人*/
    public void reduceLikePeople(Question question){
        jdbcTemplate.update("UPDATE question SET like_people=REPLACE(like_people,?,'') WHERE question_id=?",
                String.valueOf(question.getU_id())+',',question.getQuestion_id());
    }

    /****************************************排序查询************************************************/
    /*根据帖子类型查询——时间降序  个人帖子*/
    public List<Question> sortQuestionTagTimeDesc(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE tag=? AND u_id=? ORDER BY ask_time DESC",
                new Object[]{question.getTag(),question.getU_id()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*根据帖子类型查询——时间升序  个人帖子*/
    public List<Question> sortQuestionTagTime(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE tag=? AND u_id=? ORDER BY ask_time",
                new Object[]{question.getTag(),question.getU_id()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*根据帖子类型查询——时间点赞数降序 个人帖子*/
    public List<Question> sortQuestionTagLikeDesc(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE tag=? AND u_id=? ORDER BY like_num DESC,ask_time DESC",
                new Object[]{question.getTag(),question.getU_id()},new BeanPropertyRowMapper<>(Question.class));
    }


    /*根据上传时间查询——降序 个人帖子*/
    public List<Question> sortQuestionTimeDesc(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE u_id=? ORDER BY ask_time DESC",
                new Object[]{question.getU_id()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*根据上传时间查询——升序 个人帖子*/
    public List<Question> sortQuestionTime(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE u_id=? ORDER BY ask_time",
                new Object[]{question.getU_id()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*根据点赞数和时间查询——降序 个人帖子*/
    public List<Question> sortLikeTimeDESC(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE u_id=? ORDER BY like_num DESC,ask_time DESC",
                new Object[]{question.getU_id()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*按照分类获取所有帖子[按照点赞数排序]*/
    public List<Question> sortAllByTag(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE tag=? ORDER BY like_num DESC,ask_time DESC",
                new Object[]{question.getTag()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*获取所有帖子[按照点赞数排序]*/
    public List<Question> getAllQuestion(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question ORDER BY like_num DESC,ask_time DESC",
                new BeanPropertyRowMapper<>(Question.class));
    }


    /****************************************具体查询************************************************/
    /*根据帖子名模糊查询博客信息*/
    public List<Question> getQuestionByContent(Question question){
        return (List<Question>)jdbcTemplate.query("SELECT * FROM question WHERE content LIKE CONCAT(CONCAT('%',?,'%'))",
                new Object[]{question.getContent()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*根据帖子名唯一查询博客信息*/
    public Question getOnlyQuestionByContent(Question question){
        return (Question) jdbcTemplate.queryForObject("SELECT * FROM question WHERE content=?",
                new Object[]{question.getContent()},new BeanPropertyRowMapper<>(Question.class));
    }

    /*通过帖子id查找博客信息*/
    public Question getQuestionById(Question question){
        return (Question) jdbcTemplate.queryForObject("SELECT * FROM question WHERE question_id=?",
                new Object[]{question.getQuestion_id()},new BeanPropertyRowMapper<>(Question.class));
    }
}
