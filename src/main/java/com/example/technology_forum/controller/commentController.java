package com.example.technology_forum.controller;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Comment;
import com.example.technology_forum.model.User;
import com.example.technology_forum.service.commentService;
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
public class commentController {

    @Resource(name = "commentJDBCService")
    commentService commentService;

    @Resource(name = "userJDBCService")
    userService userService;

    /*
    * 写评论
    * @Param blog_id author_id u_id content (若不是层主则需要额外参数，层主评论的id:master_comment_id)
    * */
    @PostMapping("/writeComment")
    public @ResponseBody AjaxResponse writeComment(@RequestBody Comment comment){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date();
        log.info("writeComment{date}:"+date);
        comment.setComment_time(date);
        log.info("{commentController-writeComment-comment}:"+comment);
        commentService.writeComment(comment);
        if(commentService.getCommentByOtherId(comment)==null)  return AjaxResponse.fail("发表评论失败！");
        Comment newComment = commentService.getCommentByOtherId(comment);
        return AjaxResponse.success(newComment.getComment_id());
    }

    /*
    * 删除评论
    * @Param comment_id
    * */
    @PostMapping("/deleteComment")
    public @ResponseBody AjaxResponse deleteComment(@RequestBody Comment comment){
        if(!commentService.deleteComment(comment)){
            return AjaxResponse.fail("没有写此评论！");
        }
        else{
            /*判断是否为层主*/
            List<Comment> comment1 = commentService.getMasterCommentInfo(comment);
            if(comment1.size()!=0){
                for(Comment comment2:comment1){
                    if(comment2.getComment_id()==comment.getComment_id()){//说明是层主
                        List<Comment> child_comment = commentService.getCommonCommentINfo(comment2);//获取一般评论(comment2里面有层数和blog_id)
                        if(child_comment.size()!=0){//说明还有自评论
                            return AjaxResponse.fail("还有子评论，不能删除！");
                        }
                    }
                }
            }
            return AjaxResponse.success(true);
        }
    }

    /*
    * 点赞
    * @Param u_id  comment_id typeOfLike
    * */
    @PostMapping("/changeCommentLike")
    public @ResponseBody AjaxResponse changeLike(@RequestBody Comment comment){
        if(commentService.changeLike(comment)){
            return AjaxResponse.success(true);
        }else{
            if("add".equals(comment.getTypeOfLike())){
                return AjaxResponse.fail("你已点赞");
            }
            else{
                return AjaxResponse.fail("你还没有点赞");
            }
        }
    }


    /*
    * 获取博客下的所有评论
    * @Param blog_id u_id
    * */
    @PostMapping("/getCommentInfo")
    public @ResponseBody AjaxResponse getCommentInfo(@RequestBody Comment comment){
        List<Comment> commentMasterList = commentService.getMasterCommentInfo(comment);//层主博客
        if(commentMasterList.size()==0)  return AjaxResponse.fail("该博客下还无评论");
        Map<Object,Object> master_map = new HashMap<>();//层主评论
        List<Map<Object,Object>> result_list = new ArrayList<>();
        for (Comment comment1:commentMasterList){//遍历层主评论
            comment1.setU_id(comment.getU_id());
            master_map = getCommentMap(comment1);//记录层主评论的基本信息

            /*层主评论的子评论(一般评论)*/
            List<Comment> common_list = new ArrayList<>();//一般评论的列表
            List<Map<Object,Object>> commons = new ArrayList<>();//该层的所有一般性评论
            Map<Object,Object> common_map = new HashMap<>();//一般的评论(经过map包装)
            comment.setDepth(comment1.getDepth());//获取层数
            common_list = commentService.getCommonCommentINfo(comment);//通过blog_id和depth获取一般评论
            for(Comment comment2:common_list){//遍历一般评论
                comment2.setU_id(comment.getU_id());
                common_map = getCommentMap(comment2);
                commons.add(common_map);//将包装好的一般评论加入新的列表
            }
            master_map.put("child_comment",commons);//将一般评论加在该层的层主的map里面
            result_list.add(master_map);//将包装好的层主评论加在层主列表中
        }
        return AjaxResponse.success(result_list);
    }


    Map<Object,Object> getCommentMap(Comment comment){
        Map<Object,Object> map = new HashMap<>();//
        //获取评论者信息
        User user = new User();
        user.setU_id(comment.getU_id());
        User user1 = userService.getUser(user);

        map.put("comment_id",comment.getComment_id());
        map.put("name",user1.getName());
        map.put("portrait",user1.getPortrait());
        map.put("content",comment.getContent());
        map.put("like_num",comment.getLike_num());
        map.put("pass_time",getDate(comment.getComment_time()));
        map.put("is_like",IsLike(comment));
        return map;
    }

    /*查找用户是否点赞*/
    boolean IsLike(Comment comment){
        String likePeople = comment.getLike_people();//点赞人的字符串
        if(likePeople==null || likePeople.equals("")){//还无人点赞
            return false;
        }
        else {
            String[] peoples = likePeople.split(",");//通过，分割
            for (String people : peoples) {//查找里面是否有
                if (people.equals(String.valueOf(comment.getU_id()))) {
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
