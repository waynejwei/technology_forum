package com.example.technology_forum.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class Special {
    private Integer u_id;
    private Integer spec_id;

    public Special(){}

}
