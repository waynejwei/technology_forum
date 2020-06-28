package com.example.technology_forum.service;

import com.example.technology_forum.dao.questionDao;
import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class questionJDBCService implements questionService {

    @Resource
    questionDao questionDao;

    /****************************************添加************************************************/
    @Override
    public void addQuestion(Question question) {
        questionDao.addQuestion(question);
    }

    /****************************************删除************************************************/
    @Override
    public void deleteQuestion(Question question) {
        questionDao.deleteQuestion(question);
    }

    /****************************************修改************************************************/
    @Override
    public AjaxResponse changeLike(Question question) {
        try{
            Question question1 = questionDao.getQuestionById(question);//获取帖子的具体内容，包括寂静点赞的人
            String like_peoples = question1.getLike_people();
            if("add".equals(question.getTypeOfLike())){//点赞
                if(!(like_peoples==null || ("").equals(like_peoples))){//有人点过赞
                    String[] peoples = like_peoples.split(",");//划分
                    for(String people:peoples){
                        if(String.valueOf(question.getU_id()).equals(people)){//该用户已经点过赞
                            return AjaxResponse.fail("你已点过赞！");
                        }
                    }
                    questionDao.updateLikePeople(question);
                }
                else{//无人点赞
                    questionDao.updateFirstLikePeople(question);//第一次点赞
                }
                questionDao.changeLike(question,1);
            }
            else{//取消点赞
                if(like_peoples==null || ("").equals(like_peoples)){//该帖子还无人点赞
                    return AjaxResponse.fail("你还没有点赞，无法取消！");
                }
                else{
                    String[] peoples = like_peoples.split(",");//划分
                    for(String people:peoples){
                        if(String.valueOf(question.getU_id()).equals(people)){//该用户已经点过赞
                            questionDao.changeLike(question,-1);
                            questionDao.reduceLikePeople(question);
                            return AjaxResponse.success(true);
                        }
                    }
                    return AjaxResponse.fail("你还没有点赞，无法取消！");
                }
            }
            return AjaxResponse.success(true);
        }catch (EmptyResultDataAccessException e){
            return AjaxResponse.fail("没有该帖子！");
        }
    }

    @Override
    public void updateQuestion(Question question) {
        Date date = new Date();
        question.setAsk_time(date);
        questionDao.updateContent(question);
    }


    /****************************************查找************************************************/
    /*根据帖子内容获取帖子id*/
    @Override
    public int getQuestionId(Question question) {
        try{
            Question question1 = questionDao.getOnlyQuestionByContent(question);
            return question1.getQuestion_id();
        }catch(EmptyResultDataAccessException e){//没有该帖子
            return -1;
        }
    }

    @Override
    public List<Question> getQuestionByContent(Question question) {
        return questionDao.getQuestionByContent(question);
    }

    /*基于时间的排序(有分类和无分类)  个人博客*/
    @Override
    public List<Question> sortQuestionByTime(Question question) {
        log.info("sortByTime{hasTag}:"+question.getTag());
        if(question.getTag().equals("all")){//查找全部
            if(question.getTypeOfTime().equals("new")) {
                return questionDao.sortQuestionTimeDesc(question);
            }
            else{
                return questionDao.sortQuestionTime(question);
            }
        }
        else{//有分类
            if(question.getTypeOfTime().equals("new")){//降序
                return questionDao.sortQuestionTagTimeDesc(question);
            }
            else{
                return questionDao.sortQuestionTagTime(question);
            }
        }
    }

    /*基于点赞数,时间降序(有分类和无分类)  个人博客*/
    @Override
    public List<Question> sortQuestionByLike(Question question) {
        if(question.getTag().equals("all")){//查找全部的
            return questionDao.sortLikeTimeDESC(question);
        }
        else{
            return questionDao.sortQuestionTagLikeDesc(question);//有分类的
        }
    }

    /*查找所有博客(有分类无分类)*/
    @Override
    public List<Question> getAllQuestion(Question question) {
        if(question.getTag().equals("all")){
            return questionDao.getAllQuestion(question);
        }
        else{
            return questionDao.sortAllByTag(question);
        }
    }

    /****************************************查找单个帖子具体信息************************************************/
    @Override
    public Question getQuestionDetail(Question question) {
        try{
            return questionDao.getQuestionById(question);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }


    @Override
    public Question getOnlyQuestionByContent(Question question) {
        try{
            return questionDao.getOnlyQuestionByContent(question);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
}
