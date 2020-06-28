package com.example.technology_forum.service;

import com.example.technology_forum.model.Answer;

import java.util.List;

public interface answerService {

    void writeAnswer(Answer answer);

    boolean deleteAnswer(Answer answer);

    /*点赞*/
    boolean changeLike(Answer answer);

    List<Answer> getMasterAnswerInfo(Answer answer);

    List<Answer> getCommonAnswerINfo(Answer answer);

    Answer getAnswerByOtherId(Answer answer);
}
