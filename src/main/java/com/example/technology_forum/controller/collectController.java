package com.example.technology_forum.controller;

import com.example.technology_forum.dao.favoritesDao;
import com.example.technology_forum.model.*;
import com.example.technology_forum.service.blogService;
import com.example.technology_forum.service.collectService;
import com.example.technology_forum.service.questionService;
import com.example.technology_forum.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class collectController {

    @Resource(name = "collectJDBCService")
    collectService collectService;

    @Resource(name = "blogJDBCService")
    blogService blogService;

    @Resource(name = "questionJDBCService")
    questionService questionService;

    @Resource(name = "userJDBCService")
    userService userService;


    /****************************************收藏夹的管理************************************************/

    /*
     * 创建收藏夹
     * @Param favorites_name u_id
     */

    @PostMapping("/createFavorites")
    public @ResponseBody AjaxResponse createFavorites(@RequestBody Favorites favorites){
        return collectService.createFavorites(favorites);
    }

    /*
    * 删除收藏夹(同时删除里面的内容)
    * @Param favorites_id
    * */
    @PostMapping("/deleteFavorites")
    public @ResponseBody AjaxResponse deleteFavorites(@RequestBody Favorites favorites){
        collectService.deleteFavorites(favorites);
        return AjaxResponse.success(true);
    }

    /*
    * 修改收藏夹名字
    * @Param favorites_name favorites_id
    * */
    @PostMapping("/updateFavoritesName")
    public @ResponseBody AjaxResponse updateFavoritesName(@RequestBody Favorites favorites){
        collectService.updateFavoritesName(favorites);
        return AjaxResponse.success(true);
    }

    /*
    * 查找用户收藏夹
    * @Param u_id
    * */
    @PostMapping("/getUserFavorites")
    public @ResponseBody AjaxResponse getUserFavorites(@RequestBody Favorites favorites){
        List<Favorites> userFavoritesList = collectService.getFavorites(favorites);
        if(userFavoritesList.size()==0){//还没有默认的收藏夹
            Favorites newFavorites = new Favorites();
            newFavorites.setFavorites_name("我的收藏夹");
            newFavorites.setU_id(favorites.getU_id());
            collectService.createFavorites(newFavorites);
            newFavorites.setFavorites_id(collectService.getFavoritesId(newFavorites).getFavorites_id());
            userFavoritesList.add(newFavorites);
        }
        return AjaxResponse.success(userFavoritesList);
    }


    /****************************************收藏的功能***********************************************/

    /*
    * 收藏博客/帖子
    * @Param u_id favorites_id item content_id collection_name
    * */
    @PostMapping("/collectObject")
    public @ResponseBody AjaxResponse collectObject(@RequestBody Collection collection){
        return collectService.collectObject(collection);
    }

    /*
    * 删除收藏
    * @Param collection_id
    * */
    @PostMapping("/deleteCollection")
    public @ResponseBody AjaxResponse deleteCollection(@RequestBody Collection collection){
        collectService.deleteCollection(collection);
        return AjaxResponse.success(true);
    }

    /*
    * 修改收藏的名字
    * @Param collection_id collection_name
    * */
    @PostMapping("/updateCollectionName")
    public @ResponseBody AjaxResponse updateCollectionName(@RequestBody Collection collection){
        collectService.updateCollection(collection);
        return AjaxResponse.success(true);
    }


    /*
    * 查找某一收藏夹下的所有收藏
    * @Param favorites_id
    * */
    @PostMapping("getFavoritesCollection")
    public @ResponseBody AjaxResponse getFavoritesCollection(@RequestBody Collection collection){
        List<Collection> collection_list = collectService.getFavoritesCollection(collection);
        if(collection_list.size()==0){
            return AjaxResponse.success("该收藏夹还暂时没有收藏");
        }
        return AjaxResponse.success(collection_list);
    }


    /*
    * 通过id获取收藏具体信息
    * @Param collection_id
    * */
    @PostMapping("/getCollectionById")
    public @ResponseBody AjaxResponse getCollectionById(@RequestBody Collection collection){
        Collection collection1 = collectService.getCollectionById(collection);
        if(collection1==null)  return AjaxResponse.fail("没有改收藏");
        Map<Object,Object> map = new HashMap<>();
        map.put("collection_name",collection1.getCollection_name());
        map.put("collection_time",collection1.getCollection_time());
        String item = collection1.getItem();
        if("blog".equals(item)){
            Blog blog = new Blog();
            blog.setBlog_id(collection1.getContent_id());
            Blog blog1 = blogService.getBlogDetail(blog);
            map.put("blogName",blog1.getName());
            /*获取名字*/
            User user = new User();
            user.setU_id(blog1.getU_id());
            User user1 = userService.getUser(user);
            map.put("blogAuthor",user1.getName());

            /*设置点赞*/
            String likePeople = blog1.getLikePeople();//点赞人的字符串
            if(likePeople==null || likePeople.equals("")){//还无人点赞
                blog1.setIs_like("false");
            }
            else {
                String[] peoples = likePeople.split(",");//通过，分割
                boolean flag = false;
                for (String people : peoples) {//查找里面是否有
                    if (people.equals(String.valueOf(blog.getU_id()))) {
                        blog1.setIs_like("true");//该用户点赞
                        flag = true;
                    }
                }
                if (!flag) blog1.setIs_like("false");
            }
            map.put("isLike",blog1.getIs_like());
            map.put("tag",blog1.getTag());
        }
        else{
            Question question = new Question();
            question.setQuestion_id(collection1.getContent_id());
            Question question1 = questionService.getQuestionDetail(question);
            map.put("questionContent",question1.getContent());

            /*获取名字*/
            User user = new User();
            user.setU_id(question1.getU_id());
            User user1 = userService.getUser(user);
            map.put("questionAuthor",user1.getName());

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
            map.put("isLike",question1.getIs_like());
            map.put("tag",question1.getTag());
        }
        return AjaxResponse.success(map);
    }


}
