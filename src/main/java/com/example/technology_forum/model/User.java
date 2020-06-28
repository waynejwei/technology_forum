package com.example.technology_forum.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class User {
    private Integer u_id;
    private String name;
    private String password;
    private String email;
    private String sex;
    private String information;
    private Integer level;
    private String birth;
    private String VerifyCode;
    private String portrait;
    private Integer Grade;

    public User(){}
}
