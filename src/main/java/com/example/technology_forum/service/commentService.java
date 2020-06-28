package com.example.technology_forum.service;

import com.example.technology_forum.model.Comment;

import java.util.List;

public interface commentService {

    void writeComment(Comment comment);

    boolean deleteComment(Comment comment);

    /*点赞*/
    boolean changeLike(Comment comment);

    List<Comment> getMasterCommentInfo(Comment comment);

    List<Comment> getCommonCommentINfo(Comment comment);

    Comment getCommentByOtherId(Comment comment);
}
