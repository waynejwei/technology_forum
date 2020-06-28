package com.example.technology_forum.dao;

import com.example.technology_forum.model.Collection;
import com.example.technology_forum.model.Favorites;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class collectionDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    /****************************************增加************************************************/
    /*收藏博客/帖子*/
    public void collectObject(Collection collection){
        jdbcTemplate.update("INSERT INTO collection(u_id,favorites_id,item,content_id,collection_time,collection_name) " +
                "VALUES(?,?,?,?,?,?)",
                collection.getU_id(),
                collection.getFavorites_id(),
                collection.getItem(),
                collection.getContent_id(),
                collection.getCollection_time(),
                collection.getCollection_name());
    }

    /****************************************删除************************************************/
    /*删除收藏*/
    public void deleteCollection(Collection collection){
        jdbcTemplate.update("DELETE FROM collection WHERE collection_id=?",collection.getCollection_id());
    }

    /****************************************修改************************************************/
    /*修改收藏名称*/
    public void updateCollection(Collection collection){
        jdbcTemplate.update("UPDATE collection SET collection_name=? WHERE collection_id=?",
                collection.getCollection_name(),collection.getCollection_id());
    }

    /*移动收藏至我的收藏*/
    public void moveToMyFavorites(Collection collection){
        jdbcTemplate.update("UPDATE collection SET favorites_id=?,collection_name=? WHERE collection_id=?",
                collection.getFavorites_id(),
                collection.getCollection_name(),
                collection.getCollection_id());
    }

    /****************************************查找************************************************/
    /*获取某一收藏夹下的所有收藏*/
    public List<Collection> getFavoritesCollection(Collection collection){
        return (List<Collection>)jdbcTemplate.query("SELECT * FROM collection WHERE favorites_id=? ORDER BY collection_time DESC",
                new Object[]{collection.getFavorites_id()},new BeanPropertyRowMapper<>(Collection.class));
    }

    /*获取收藏夹id*/
    public Collection getCollectionId(Collection collection){
        return (Collection)jdbcTemplate.queryForObject("SELECT * FROM collection WHERE u_id=? and favorites_id=? and collection_name=?",
                new Object[]{collection.getU_id(),collection.getFavorites_id(),collection.getCollection_name()},new BeanPropertyRowMapper<>(Collection.class));
    }


    /*通过id获取收藏夹*/
    public Collection getCollectionById(Collection collection){
        return (Collection)jdbcTemplate.queryForObject("SELECT * FROM collection WHERE collection_id=?",
                new Object[]{collection.getCollection_id()},new BeanPropertyRowMapper<>(Collection.class));
    }

    /*寻找内容一样的收藏*/
    public Collection getSameCollection(Collection collection){
        return (Collection)jdbcTemplate.queryForObject("SELECT * FROM collection WHERE u_id=? and favorites_id=? and collection_name=? " +
                "and item=? and content_id=?",
                new Object[]{collection.getU_id(),collection.getFavorites_id(),collection.getCollection_name(),collection.getItem(),collection.getContent_id()},
                new BeanPropertyRowMapper<>(Collection.class));
    }


}
