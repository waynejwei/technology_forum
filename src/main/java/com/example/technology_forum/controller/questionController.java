package com.example.technology_forum.controller;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Question;
import com.example.technology_forum.model.User;
import com.example.technology_forum.service.questionService;
import com.example.technology_forum.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
public class questionController {

    @Resource(name = "questionJDBCService")
    questionService questionService;

    @Resource(name = "userJDBCService")
    userService userService;

    /*
    * 写帖子
    * @Param u_id content tag
    * */
    @PostMapping("/addQuestion")
    public @ResponseBody AjaxResponse addQuestion(@RequestBody Question question){
        Question question1 = questionService.getOnlyQuestionByContent(question);
        if(question1==null){
            Date date = new Date();
            question.setAsk_time(date);
            questionService.addQuestion(question);
            Question question2 = questionService.getOnlyQuestionByContent(question);
            return AjaxResponse.success(String.valueOf(question2.getQuestion_id()));
        }
        else{
            int question_id = question1.getQuestion_id();
            return AjaxResponse.fail(String.valueOf(question_id));
        }
    }

    /*
    * 删除帖子
    * @Param question_id
    * */
    @PostMapping("/deleteQuestion")
    public @ResponseBody AjaxResponse deleteQuestion(@RequestBody Question question){
        Question question1 = questionService.getQuestionDetail(question);
        if(question1==null)  return AjaxResponse.fail("没有该帖子！");
        questionService.deleteQuestion(question);
        return AjaxResponse.success(true);
    }


    /*
    * 修改帖子的内容
    * @Param question_id content
    * */
    @PostMapping("/updateQuestionContent")
    public @ResponseBody AjaxResponse updateQuestionContent(@RequestBody Question question){
        questionService.updateQuestion(question);
        return AjaxResponse.success(true);
    }

    /*
    * 点赞
    * @Param u_id question_id  typeOfLike
    * */
    @PostMapping("/updateQuestionLike")
    public @ResponseBody AjaxResponse updateQuestionLike(@RequestBody Question question){
        return questionService.changeLike(question);
    }

    /*返回简写的帖子信息*/
    public Map<Object, Object> getSortQuestionInfo(Question question){
        Map<Object, Object> map = new HashMap<>();
        map.put("question_id",question.getQuestion_id());
        map.put("content",question.getContent());
        map.put("tag",question.getTag());
        map.put("author_name",question.getAuthor_name());
        map.put("like_num",question.getLike_num());
        map.put("ask_time",question.getAsk_time());
        return map;
    }

    /*
    * 根据帖子内容返回帖子
    * @Param content
    * */
    @PostMapping("/getQuestionByContent")
    public @ResponseBody AjaxResponse getQuestionByContent(@RequestBody Question question){
        List<Question> list = questionService.getQuestionByContent(question);//通过内容模糊查询
        List<Map<Object, Object>> result_list = new ArrayList<>();
        for (Question question1 : list) {
            /*获取作者的名字*/
            int author_id = question1.getU_id();//获取作者的u_id
            User user = new User();
            user.setU_id(author_id);
            String author_name = userService.getName(user);
            question1.setAuthor_name(author_name);

            Map<Object, Object> map = getSortQuestionInfo(question1);
            result_list.add(map);
        }
        return AjaxResponse.success(result_list);
    }


    /*
     * 通过时间和点赞数来查询,个人帖子
     * @Param tag  u_id(作者的id)
     * @Return author_id
     * */
    @PostMapping("/sortQuestionByLike")
    public @ResponseBody AjaxResponse sortQuestionByLike(@RequestBody Question question){
        List<Question> list = questionService.sortQuestionByLike(question);   //作者所写的帖子
        List<Map<Object, Object>> result_list = new ArrayList<>();
        for (Question question1 : list) {
            /*获取作者的名字*/
            int author_id = question1.getU_id();//获取作者的u_id
            User user = new User();
            user.setU_id(author_id);
            String author_name = userService.getName(user);
            question1.setAuthor_name(author_name);

            Map<Object, Object> map = getSortQuestionInfo(question1);
            result_list.add(map);
        }
        return AjaxResponse.success(result_list);
    }


    /*
     * 通过时间来查询，个人帖子
     * @Param tag typeOfTime u_id
     * */
    @PostMapping("/sortQuestionByTime")
    public @ResponseBody AjaxResponse sortQuestionByTime(@RequestBody Question question){
        List<Question> list = questionService.sortQuestionByTime(question);
        List<Map<Object, Object>> result_list = new ArrayList<>();
        for (Question question1 : list) {
            /*获取作者的名字*/
            int author_id = question1.getU_id();//获取作者的u_id
            User user = new User();
            user.setU_id(author_id);
            String author_name = userService.getName(user);
            question1.setAuthor_name(author_name);

            Map<Object, Object> map = getSortQuestionInfo(question1);
            result_list.add(map);
        }
        return AjaxResponse.success(result_list);
    }

    /*
     * 按照分类获取所有帖子[按照点赞数排序]
     * @Param u_id  tag
     * */
    @PostMapping("/getAllQuestion")
    public @ResponseBody AjaxResponse getAllBlog(@RequestBody Question question){
        List<Question> list = questionService.getAllQuestion(question);
        List<Map<Object, Object>> result_list = new ArrayList<>();
        if(list.size()!=0){
            //每一个博客
            for (Question result_blog : list) {
                /*获取作者的名字*/
                int author_id = result_blog.getU_id();//获取作者的u_id
                User user = new User();
                user.setU_id(author_id);
                String author_name = userService.getName(user);
                result_blog.setAuthor_name(author_name);

                Map<Object, Object> map = getSortQuestionInfo(result_blog);
                result_list.add(map);
            }
        }
        return AjaxResponse.success(result_list);
    }

    /*
     * 通过博客id返回博客的所有信息
     * @Param question_id  u_id(便于查看该用户是否点赞)
     * */
    @PostMapping("/getQuestionDetail")
    public @ResponseBody AjaxResponse getQuestionDetail(@RequestBody Question question){
        Question question1 = questionService.getQuestionDetail(question);//根据博客id获取博客信息
        if(question1==null)  return AjaxResponse.fail("没有此博客");
        /*获取作者的名字*/
        int author_id = question1.getU_id();//获取作者的u_id
        User user = new User();
        user.setU_id(author_id);
        String author_name = userService.getName(user);
        question1.setAuthor_name(author_name);

        /*查找此人是否点赞*/
        String likePeople = question1.getLike_people();//点赞人的字符串
        if(likePeople==null || likePeople.equals("")){//还无人点赞
            question1.setIs_like("false");
        }
        else {
            String[] peoples = likePeople.split(",");//通过，分割
            boolean flag = false;
            for (String people : peoples) {//查找里面是否有
                if (people.equals(String.valueOf(question.getU_id()))) {
                    question1.setIs_like("true");//该用户点赞
                    flag = true;
                }
            }
            if (!flag) question1.setIs_like("false");
        }

        Map<Object, Object> map = getSortQuestionInfo(question1);
        map.put("content",question1.getContent());
        map.put("is_like",question1.getIs_like());

        return AjaxResponse.success(map);
    }
}
