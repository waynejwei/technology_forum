package com.example.technology_forum.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class Answer {

    private Integer answer_id;
    private Integer question_id;
    private Integer author_id;  //作者
    private Integer u_id;//评论的人
    private String content;
    private Integer like_num;  //回答被点赞的个数
    private String like_people;
    private Date answer_time;
    private Integer depth;//层数
    private Integer story_master;//层主id
    private Boolean is_master;//是否为层主
    private int room;//房间号
    //帖子id、层数、房间号作为候选码

    private String typeOfLike;//取消点赞还是点赞
    private String is_like;//是否点赞
    private Integer master_answer_id;//层主回答的id

    public Answer(){}
}
