package com.example.technology_forum.dao;

import com.example.technology_forum.model.Goods;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class goodsDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    /*增加商品*/
    public void addGoods(Goods goods){
        jdbcTemplate.update("INSERT INTO goods(goods_name,description,update_time,update_person) VALUES(?,?,?,?)",
                goods.getGoods_name(),
                goods.getDescription(),
                goods.getUpdate_person(),
                goods.getUpdate_person());
    }

    /*删除商品*/
    public void deleteGoods(Goods goods){
        jdbcTemplate.update("DELETE FROM goods WHERE goods_id=?",goods.getGoods_id());
    }

    /*修改商品名*/
    public void updateGoodsName(Goods goods){
        jdbcTemplate.update("UPDATE goods SET goods_name=?,update_time=?,update_person WHERE goods_id=?",
                goods.getGoods_name(),
                goods.getUpdate_time(),
                goods.getUpdate_person(),
                goods.getGoods_id());
    }

    /*修改商品描述*/
    public void updateGoodsDescription(Goods goods){
        jdbcTemplate.update("UPDATE goods SET description=?,update_time=?,update_person=? WHERE goods_id=?",
                goods.getDescription(),
                goods.getUpdate_time(),
                goods.getUpdate_person(),
                goods.getGoods_id());
    }

    /*查找商品列表*/
    public List<Goods> getAllGoods(){
        return (List<Goods>) jdbcTemplate.query("SELECT * FROM goods",new BeanPropertyRowMapper<>(Goods.class));
    }

    /*查找具体商品*/
    public Goods getGoods(Goods goods){
        return (Goods) jdbcTemplate.queryForObject("SELECT * FROM goods WHERE goods_id=?",
                new Object[]{goods.getGoods_id()},new BeanPropertyRowMapper<>(Goods.class));
    }

    /*通过商品名字来查找商品*/
    public Goods getGoodsByName(Goods goods){
        return (Goods) jdbcTemplate.queryForObject("SELECT * FROM goods WHERE goods_name=?",
                new Object[]{goods.getGoods_name()},new BeanPropertyRowMapper<>(Goods.class));
    }


}
