package com.example.technology_forum.dao;

import com.example.technology_forum.model.Answer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class answerDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /****************************************增************************************************/
    /*给帖子写回复*/
    public void writeAnswer(Answer answer){
        jdbcTemplate.update("INSERT INTO answer(question_id,author_id,u_id,content,answer_time,depth,story_master,is_master,room) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)",
                answer.getQuestion_id(),
                answer.getAuthor_id(),
                answer.getU_id(),
                answer.getContent(),
                answer.getAnswer_time(),
                answer.getDepth(),
                answer.getStory_master(),
                answer.getIs_master(),
                answer.getRoom());
    }

    /****************************************删************************************************/
    /*通过id删除回复*/
    public void deleteAnswer(Answer answer){
        jdbcTemplate.update("DELETE FROM answer WHERE answer_id=?",answer.getAnswer_id());
    }


    /****************************************改************************************************/
    /*给回复点赞*/
    public void updateAnswerLikeNum(Answer answer,int addNum){
        jdbcTemplate.update("UPDATE answer SET like_num=like_num+? WHERE answer_id=?",addNum,answer.getAnswer_id());
    }

    /*点赞后加入到likePeople——第一次*/
    public void addFirstLikePeople(Answer answer){
        jdbcTemplate.update("UPDATE answer SET like_people=? WHERE answer_id=?",answer.getU_id(),answer.getAnswer_id());
        jdbcTemplate.update("UPDATE answer SET like_people=CONCAT(like_people,',') WHERE answer_id=?",answer.getAnswer_id());
    }

    /*点赞后加入打到likePeople*/
    public void addLikePeople(Answer answer){
        jdbcTemplate.update("UPDATE answer SET like_people=CONCAT(like_people,?) WHERE answer_id=?",
                answer.getU_id(),answer.getAnswer_id());
        jdbcTemplate.update("UPDATE answer SET like_people=CONCAT(like_people,',') WHERE answer_id=?",answer.getAnswer_id());
    }

    /*取消点赞*/
    public void reduceLikePeople(int answer_id,String u_id){
        jdbcTemplate.update("UPDATE answer SET like_people=REPLACE(like_people,?,'') WHERE answer_id=?",u_id,answer_id);
    }

    /****************************************查************************************************/
    /*根据帖子id查看层主评论信息(点赞降序)*/
    public List<Answer> getMasterAnswerInfo(Answer answer){
        return (List<Answer>)jdbcTemplate.query("SELECT * FROM answer WHERE question_id=? AND is_master=true ORDER BY like_num DESC,answer_time DESC",
                new Object[]{answer.getQuestion_id()},new BeanPropertyRowMapper<>(Answer.class));
    }

    /*根据帖子id和层数来获取一般回复的信息(时间降序)*/
    public List<Answer> getCommonAnswerInfo(Answer answer){
        return (List<Answer>)jdbcTemplate.query("SELECT * FROM answer WHERE question_id=? AND is_master=false AND depth=? ORDER BY answer_time DESC",
                new Object[]{answer.getQuestion_id(),answer.getDepth()},new BeanPropertyRowMapper<>(Answer.class));
    }

    /*通过answer_id来查看回复*/
    public Answer getAnswerById(Answer answer){
        return (Answer) jdbcTemplate.queryForObject("SELECT * FROM answer WHERE answer_id=?",
                new Object[]{answer.getAnswer_id()},new BeanPropertyRowMapper<>(Answer.class));
    }

    /*根据question_id,depth,room获取回复
    * */
    public Answer getAnswerByOtherId(Answer answer){
        return (Answer) jdbcTemplate.queryForObject("SELECT * FROM answer WHERE question_id=? and depth=? and room=?",
                new Object[]{answer.getQuestion_id(),answer.getDepth(),answer.getRoom()},new BeanPropertyRowMapper<>(Answer.class));
    }

    /*查找已有回复的层数*/
    public Integer getDepth(Answer answer){
        return (Integer)jdbcTemplate.queryForObject("SELECT MAX(depth) FROM answer WHERE question_id=? GROUP BY question_id",
                new Object[]{answer.getQuestion_id()},Integer.class);
    }

    /*查找当前层数的已有的房间号*/
    public Integer getRoom(Answer answer){
        return (Integer)jdbcTemplate.queryForObject("SELECT MAX(room) FROM answer WHERE question_id=? and depth=?",
                new Object[]{answer.getQuestion_id(),answer.getDepth()},Integer.class);
    }

    /*获取用户的评论*/
    public Integer getUserAnswerNum(Answer answer){
        return (Integer) jdbcTemplate.queryForObject("SELECT COUNT(answer_id) FROM answer WHERE u_id=?",
                new Object[]{answer.getU_id()},Integer.class);
    }

    /*查看用户的所有评论*/
    public List<Answer> getUserAnswer(Answer answer){
        return (List<Answer>)jdbcTemplate.query("SELECT * FROM answer WHERE u_id=? ORDER BY like_num DESC,answer_time DESC",
                new Object[]{answer.getU_id()},new BeanPropertyRowMapper<>(Answer.class));
    }
}
