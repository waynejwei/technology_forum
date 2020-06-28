package com.example.technology_forum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class Blog {

    private Integer blog_id;
    private Integer u_id;
    private String name;
    private String author_name;
    private String content;
    private String html;
    private Integer like_num;
    private String tag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date upload_time;
    private String likePeople;
    private String typeOfLike;
    private String typeOfTime;
    private String is_like;

    public Blog(){}
}

