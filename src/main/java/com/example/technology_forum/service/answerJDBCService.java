package com.example.technology_forum.service;

import com.example.technology_forum.dao.answerDao;
import com.example.technology_forum.model.Answer;
import com.example.technology_forum.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class answerJDBCService implements answerService {

    @Resource
    answerDao answerDao;

    @Resource
    questionService questionService;
    @Override
    public void writeAnswer(Answer answer) {

        Question question = new Question();
        question.setQuestion_id(answer.getQuestion_id());
        Question question1 = questionService.getQuestionDetail(question);//为了获取写帖子人的id
        if((answer.getAuthor_id()).equals(question1.getU_id())){//说明该用户是层主
            answer.setStory_master(answer.getU_id());
            try {
                answer.setDepth(answerDao.getDepth(answer)+1);//当前层数+1
            }catch(EmptyResultDataAccessException e){
                answer.setDepth(1);
            }
            answer.setIs_master(true);
            answer.setRoom(1);//层主的房间号为1
        }
        else{//不为层主，需要额外参数层主评论的id
            Answer answer1 = new Answer();
            answer1.setAnswer_id(answer.getMaster_answer_id());//填上层主回复的id
            Answer master_answer = answerDao.getAnswerById(answer1);//获取层主的回复
            answer.setStory_master(master_answer.getStory_master());//设置层主
            answer.setDepth(master_answer.getDepth());//层主才知道层数
            answer.setIs_master(false);
            try{
                answer.setRoom(answerDao.getRoom(answer)+1);//房间号为当前最大房间号+1
            }catch (EmptyResultDataAccessException e){
                answer.setRoom(2);
            }
        }
        log.info("{answerService-writeAnswer-answer}:"+answer);
        answerDao.writeAnswer(answer);
    }

    @Override
    public boolean deleteAnswer(Answer answer) {
        try{
            Answer answer1 = answerDao.getAnswerById(answer);
            boolean is_master = answer1.getIs_master();
            if(is_master){//是层主，删除时连同子回复一起删除
                List<Answer> child_answer = answerDao.getCommonAnswerInfo(answer1);
                for(Answer answer2:child_answer){
                    answerDao.deleteAnswer(answer2);//删除子回复
                }
            }
            answerDao.deleteAnswer(answer1);//删除层主回复
        }catch(EmptyResultDataAccessException e){
            return false;//没有此回复
        }
        return true;
    }

    @Override
    public boolean changeLike(Answer answer) {
        Answer answer1 = answerDao.getAnswerById(answer);//获取回复的详细信息
        String like_people = answer1.getLike_people();
        if("add".equals(answer.getTypeOfLike())){//点赞
            if(like_people==null || like_people.equals("")){//目前还没有人点赞
                answerDao.updateAnswerLikeNum(answer,1);
                answerDao.addFirstLikePeople(answer);
            }
            else{
                String[] peoples = like_people.split(",");
                for (String people:peoples) {
                    if(String.valueOf(answer.getU_id()).equals(people)){
                        return false;//已经点赞
                    }
                }
                answerDao.updateAnswerLikeNum(answer,1);
                answerDao.addLikePeople(answer);
            }
            return true;
        }
        else{//取消点赞
            if((like_people == null) || ("").equals(like_people)){//没有人点赞，取消不了
                return false;
            }
            else{
                String[] peoples = like_people.split(",");
                for (String people:peoples) {
                    if(String.valueOf(answer.getU_id()).equals(people)){
                        answerDao.reduceLikePeople(answer.getAnswer_id(),String.valueOf(answer.getU_id())+',');
                        answerDao.updateAnswerLikeNum(answer,-1);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public List<Answer> getMasterAnswerInfo(Answer answer) {
        return answerDao.getMasterAnswerInfo(answer);
    }

    @Override
    public List<Answer> getCommonAnswerINfo(Answer answer) {
        return answerDao.getCommonAnswerInfo(answer);
    }

    @Override
    public Answer getAnswerByOtherId(Answer answer) {
        try{
            return answerDao.getAnswerByOtherId(answer);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
}
