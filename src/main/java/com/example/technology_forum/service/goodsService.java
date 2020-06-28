package com.example.technology_forum.service;

import com.example.technology_forum.model.Goods;

import java.util.List;

public interface goodsService {

    public void addGoods(Goods goods);

    public void deleteGoods(Goods goods);

    public void updateGoodsInfo(Goods goods);

    public List<Goods> getAllGoods();

    public Goods getGoods(Goods goods);

    public Goods getGoodsByName(Goods goods);
}
