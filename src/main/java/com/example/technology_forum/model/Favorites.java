package com.example.technology_forum.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class Favorites {

    private Integer favorites_id;
    private String favorites_name;
    private int u_id;

    public Favorites(){}
}
