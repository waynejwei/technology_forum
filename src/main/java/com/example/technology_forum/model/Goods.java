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
public class Goods {
    private Integer goods_id;
    private String goods_name;
    private String description;
    private Date update_time;
    private String update_person;//更新者的名字

    public Goods(){}
}
