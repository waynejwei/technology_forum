package com.example.technology_forum.controller;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Answer;
import com.example.technology_forum.model.User;
import com.example.technology_forum.service.answerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
public class answerController {

    @Resource(name = "answerJDBCService")
    answerService answerService;

    @Resource(name = "userJDBCService")
    com.example.technology_forum.service.userService userService;

    /*
     * 写回复
     * @Param question_id author_id u_id content (若不是层主则需要额外参数，层主评论的id:master_answer_id)
     * */
    @PostMapping("/writeAnswer")
    public @ResponseBody
    AjaxResponse writeAnswer(@RequestBody Answer answer){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date();
        log.info("writeAnswer{date}:"+date);
        answer.setAnswer_time(date);
        log.info("{answerController-writeAnswer-answer}:"+answer);
        answerService.writeAnswer(answer);
        if(answerService.getAnswerByOtherId(answer)==null)  return AjaxResponse.fail("发表回复失败！");
        Answer newAnswer = answerService.getAnswerByOtherId(answer);
        return AjaxResponse.success(newAnswer.getAnswer_id());
    }

    /*
     * 删除回复
     * @Param answer_id
     * */
    @PostMapping("/deleteAnswer")
    public @ResponseBody AjaxResponse deleteAnswer(@RequestBody Answer answer){
        if(!answerService.deleteAnswer(answer)){
            return AjaxResponse.fail("没有写此回复！");
        }
        else{
            /*判断是否为层主*/
            List<Answer> answer1 = answerService.getMasterAnswerInfo(answer);
            if(answer1.size()!=0){
                for(Answer answer2:answer1){
                    if(answer2.getAnswer_id()==answer.getAnswer_id()){//说明是层主
                        List<Answer> child_answer = answerService.getCommonAnswerINfo(answer2);//获取一般评论(answer2里面有层数和question_id)
                        if(child_answer.size()!=0){//说明还有子回复
                            return AjaxResponse.fail("还有子回复，不能删除！");
                        }
                    }
                }
            }
            return AjaxResponse.success(true);
        }
    }

    /*
     * 点赞
     * @Param u_id  answer_id typeOfLike
     * */
    @PostMapping("/changeAnswerLike")
    public @ResponseBody AjaxResponse changeAnswerLike(@RequestBody Answer answer){
        if(answerService.changeLike(answer)){
            return AjaxResponse.success(true);
        }else{
            if("add".equals(answer.getTypeOfLike())){
                return AjaxResponse.fail("你已点赞");
            }
            else{
                return AjaxResponse.fail("你还没有点赞");
            }
        }
    }


    /*
     * 获取帖子下的所有评论
     * @Param question_id u_id
     * */
    @PostMapping("/getAnswerInfo")
    public @ResponseBody AjaxResponse getAnswerInfo(@RequestBody Answer answer){
        List<Answer> answerMasterList = answerService.getMasterAnswerInfo(answer);//层主博客
        if(answerMasterList.size()==0)  return AjaxResponse.fail("该帖子下还无回复");
        Map<Object,Object> master_map = new HashMap<>();//层主评论
        List<Map<Object,Object>> result_list = new ArrayList<>();
        for (Answer answer1:answerMasterList){//遍历层主评论
            answer1.setU_id(answer.getU_id());
            master_map = getAnswerMap(answer1);//记录层主评论的基本信息

            /*层主评论的子评论(一般评论)*/
            List<Answer> answer_list = new ArrayList<>();//一般评论的列表
            List<Map<Object,Object>> answers = new ArrayList<>();//该层的所有一般性评论
            Map<Object,Object> answer_map = new HashMap<>();//一般的评论(经过map包装)
            answer.setDepth(answer1.getDepth());//获取层数
            answer_list = answerService.getCommonAnswerINfo(answer);//通过blog_id和depth获取一般评论
            for(Answer answer2:answer_list){//遍历一般评论
                answer2.setU_id(answer.getU_id());
                answer_map = getAnswerMap(answer2);
                answers.add(answer_map);//将包装好的一般评论加入新的列表
            }
            master_map.put("child_answer",answers);//将一般评论加在该层的层主的map里面
            result_list.add(master_map);//将包装好的层主评论加在层主列表中
        }
        return AjaxResponse.success(result_list);
    }


    Map<Object,Object> getAnswerMap(Answer answer){
        Map<Object,Object> map = new HashMap<>();//
        //获取评论者信息
        User user = new User();
        user.setU_id(answer.getU_id());
        User user1 = userService.getUser(user);

        map.put("name",user1.getName());
        map.put("portrait",user1.getPortrait());
        map.put("content",answer.getContent());
        map.put("like_num",answer.getLike_num());
        map.put("pass_time",getDate(answer.getAnswer_time()));
        map.put("is_like",IsLike(answer));
        return map;
    }

    /*查找用户是否点赞*/
    boolean IsLike(Answer answer){
        String likePeople = answer.getLike_people();//点赞人的字符串
        if(likePeople==null || likePeople.equals("")){//还无人点赞
            return false;
        }
        else {
            String[] peoples = likePeople.split(",");//通过，分割
            for (String people : peoples) {//查找里面是否有
                if (people.equals(String.valueOf(answer.getU_id()))) {
                    return true;
                }
            }
            return false;
        }
    }


    /*计算日期差，评论过了多久*/
    String getDate(Date old){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Date now = new Date();
        long between = (now.getTime()-old.getTime())/1000;//除1000是为了转换为秒
        long day1=between/(24*3600);
        long hour1=between%(24*3600)/3600;
        long minute1=between%3600/60;
        long second1=between%60/60;
        if(day1>=1){
            if(day1<30){
                return day1+"天前";
            }
            else if(day1<365){
                return day1/30+"个月前";
            }
            else{
                return day1/365+"年前";
            }
        }
        else{
            if(hour1>=1){
                return hour1+"小时前";
            }
            else{
                if(minute1>=1){
                    return minute1+"分钟前";
                }
                else{
                    return second1+"秒前";
                }
            }
        }
    }
}
