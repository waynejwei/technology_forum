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
public class Collection {

    private Integer collection_id;
    private Integer u_id;
    private Integer favorites_id;  //收藏夹id
    private String item;
    private Integer content_id;
    private Date collection_time;
    private String collection_name;

    public Collection(){}
}
