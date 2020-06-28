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
public class Question {

    private Integer question_id;//帖子id
    private Integer u_id;
    private String content;
    private Integer like_num;
    private String like_people;
    private String tag;
    private Date ask_time;

    private String typeOfLike;
    private String typeOfTime;
    private String Author_name;
    private String is_like;
    public Question(){}
}
