package com.example.technology_forum.service;

import com.example.technology_forum.dao.goodsDao;
import com.example.technology_forum.model.Goods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class goodsJDBCService implements goodsService {

    @Resource
    goodsDao goodsDao;

    @Override
    public void addGoods(Goods goods) {
        goodsDao.addGoods(goods);
    }

    @Override
    public void deleteGoods(Goods goods) {
        goodsDao.deleteGoods(goods);
    }

    @Override
    public void updateGoodsInfo(Goods goods) {
        if(goods.getGoods_name()!=null){
            goodsDao.updateGoodsName(goods);
        }
        if(goods.getDescription()!=null){
            goodsDao.updateGoodsDescription(goods);
        }
    }

    @Override
    public List<Goods> getAllGoods() {
        return goodsDao.getAllGoods();
    }

    @Override
    public Goods getGoods(Goods goods) {
        try{
            return goodsDao.getGoods(goods);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public Goods getGoodsByName(Goods goods) {
        try{
            return goodsDao.getGoodsByName(goods);
        } catch(EmptyResultDataAccessException e){
            return null;
        }
    }


}
