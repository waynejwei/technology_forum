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
public class Comment {

    private Integer comment_id;
    private Integer blog_id;
    private Integer author_id;  //作者
    private Integer u_id;//评论的人
    private String content;
    private Integer like_num;  //评论被点赞的个数
    private String like_people;
    private Date comment_time;
    private Integer depth;//层数
    private Integer story_master;//层主id
    private Boolean is_master;//是否为层主
    private int room;//房间号
    //博客id、层数、房间号作为候选码

    private String typeOfLike;//取消点赞还是点赞
    private String is_like;//是否点赞
    private Integer master_comment_id;//层主评论的id

    public Comment(){}
}
