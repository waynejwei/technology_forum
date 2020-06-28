package com.example.technology_forum.service;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Question;

import java.util.List;

public interface questionService {

    /****************************************添加************************************************/
    void addQuestion(Question question);

    /****************************************删除************************************************/
    void deleteQuestion(Question question);

    /****************************************修改************************************************/
    AjaxResponse changeLike(Question question);

    void updateQuestion(Question question);

    /****************************************查找(具体博客、排序)************************************************/
    int getQuestionId(Question question);

    List<Question> sortQuestionByTime(Question question);

    List<Question> sortQuestionByLike(Question question);

    List<Question> getAllQuestion(Question question);

    Question getQuestionDetail(Question question);

    List<Question> getQuestionByContent(Question question);

    Question getOnlyQuestionByContent(Question question);
}
