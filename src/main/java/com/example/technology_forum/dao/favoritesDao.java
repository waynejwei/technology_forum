package com.example.technology_forum.dao;

import com.example.technology_forum.model.Favorites;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class favoritesDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    /*创建收藏夹*/
    public void createFavorites(Favorites favorites){
        jdbcTemplate.update("INSERT INTO favorites(favorites_name,u_id) VALUES(?,?)",
                favorites.getFavorites_name(),
                favorites.getU_id());
    }


    /*删除收藏夹*/
    public void deleteFavorites(Favorites favorites){
        jdbcTemplate.update("DELETE FROM favorites WHERE favorites_id=?",favorites.getFavorites_id());
    }



    /*修改收藏夹名字*/
    public void updateFavoritesName(Favorites favorites){
        jdbcTemplate.update("UPDATE favorites SET favorites_name=? WHERE favorites_id=?",
                favorites.getFavorites_name(),
                favorites.getFavorites_id());
    }


    /*通过收藏夹名字和拥有者获取收藏夹*/
    public Favorites getFavoritesId(Favorites favorites){
        return jdbcTemplate.queryForObject("SELECT * FROM favorites WHERE favorites_name=? and u_id=?",
                new Object[]{favorites.getFavorites_name(),favorites.getU_id()},
                new BeanPropertyRowMapper<>(Favorites.class));
    }

    /*获取用户收藏夹*/
    public List<Favorites> getFavorites(Favorites favorites){
        return (List<Favorites>)jdbcTemplate.query("SELECT * FROM favorites WHERE u_id=?",
                new Object[]{favorites.getU_id()},new BeanPropertyRowMapper<>(Favorites.class));
    }

    /*通过id获取收藏夹*/
    public Favorites getFavoritesById(Favorites favorites){
        return (Favorites)jdbcTemplate.queryForObject("SELECT * FROM favorites WHERE favorites_id=?",
                new Object[]{favorites.getFavorites_id()},new BeanPropertyRowMapper<>(Favorites.class));
    }
}
