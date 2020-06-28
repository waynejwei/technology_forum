package com.example.technology_forum.controller;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Special;
import com.example.technology_forum.model.User;
import com.example.technology_forum.service.specialService;
import com.example.technology_forum.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class specialController {

    @Resource(name = "specialJDBCService")
    specialService specialService;

    @Resource(name = "userJDBCService")
    userService userService;

    /*
    * 关注某人
    * @Param u_id spec_id
    * */
    @PostMapping("/followPerson")
    public @ResponseBody AjaxResponse followPerson(@RequestBody Special special){
        if(special.getU_id().equals(special.getSpec_id())) {
            return AjaxResponse.fail("自己不能关注自己！");
        }
        Special Followers = specialService.hasFollowed(special);
        if(Followers!=null)  return AjaxResponse.fail("你已关注！");
        specialService.followPerson(special);
        return AjaxResponse.success(true);
    }

    /*
    * 取消关注
    * @Param u_id spec_id
    * */
    @PostMapping("/cancelFollowing")
    public @ResponseBody AjaxResponse cancelFollowing(@RequestBody Special special){
        Special Followers = specialService.hasFollowed(special);
        if(Followers==null)  return AjaxResponse.fail("你还没有关注！");
        specialService.cancelFollowing(special);
        return AjaxResponse.success(true);
    }

    /*
    * 计算该用户关注的人的个数
    * @Param u_id
    * */
    @PostMapping("/countFollowers")
    public @ResponseBody AjaxResponse countFollowers(@RequestBody Special special){
        int count =  specialService.countFollowers(special);
        Map<Object,Object> map = new HashMap<>();
        map.put("followerNum",count);
        return AjaxResponse.success(map);
    }

    /*
    * 计算该用户的粉丝个数
    * @Param u_id
    * */
    @PostMapping("/countFans")
    public @ResponseBody AjaxResponse countFans(@RequestBody Special special){
        int count = specialService.countFans(special);
        Map<Object,Object> map = new HashMap<>();
        map.put("fanNum",count);
        return AjaxResponse.success(map);
    }

    /*
    * 获取该用户的关注人的基本信息
    * @Param u_id
    * */
    @PostMapping("/getFollowers")
    public @ResponseBody AjaxResponse getFollowers(@RequestBody Special special){
        List<Special> followers = specialService.getFollowers(special);
        if(followers.size()==0)  return AjaxResponse.fail("你还没有关注任何人");
        Map<Object,Object> result_map = new HashMap<>();
        List<Map<Object,Object>> followers_info = new ArrayList<>();
        for (Special follower:followers) {
            int follower_id = follower.getSpec_id();
            User user = new User();
            user.setU_id(follower_id);
            User follow = userService.getUser(user);//获取关注的人的详细信息
            result_map.put("follower_id",follow.getU_id());
            result_map.put("follower_name",follow.getName());
            result_map.put("portrait",follow.getPortrait());
            followers_info.add(result_map);
        }
        return AjaxResponse.success(followers_info);
    }

    /*
    * 获取该用户的粉丝的基本信息
    * @Param u_id
    * */
    @PostMapping("/getFans")
    public @ResponseBody AjaxResponse getFans(@RequestBody Special special){
        List<Special> fans = specialService.getFans(special);
        if(fans.size()==0)  return AjaxResponse.fail("你还没有粉丝");
        Map<Object,Object> result_map = new HashMap<>();
        List<Map<Object,Object>> fans_info = new ArrayList<>();
        for (Special fan:fans) {
            int fan_id = fan.getU_id();
            User user = new User();
            user.setU_id(fan_id);
            User f = userService.getUser(user);//获取关注的人的详细信息
            result_map.put("fan_id",f.getU_id());
            result_map.put("fan_name",f.getName());
            result_map.put("portrait",f.getPortrait());
            fans_info.add(result_map);
        }
        return AjaxResponse.success(fans_info);
    }
}
