package com.example.technology_forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AjaxResponse  {
    private boolean isok;
    private int code;
    private String message;
    private Object data;

    public AjaxResponse(){}

    public static AjaxResponse success(Object data){
        AjaxResponse response = new AjaxResponse();
        response.setIsok(true);
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }


    public static AjaxResponse fail(String message){
        AjaxResponse response = new AjaxResponse();
        response.setIsok(false);
        response.setCode(500);
        response.setMessage(message);
        return response;
    }
}

