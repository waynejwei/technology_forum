package com.example.technology_forum.controller;


import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Goods;
import com.example.technology_forum.service.goodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class goodsController {

    @Resource(name = "goodsJDBCService")
    goodsService goodsService;

    /*
    * 增加商品
    * @Param goods_name description update_person
    * */
    @PostMapping("/addGoods")
    public @ResponseBody AjaxResponse addGoods(@RequestBody Goods goods){
        List<Goods> list = goodsService.getAllGoods();
        for(Goods goods1:list){
            String name = goods1.getGoods_name();
            if(name.equals(goods.getGoods_name())){
                return AjaxResponse.fail("商品不能重名");
            }
        }
        Date date = new Date();
        goods.setUpdate_time(date);
        goodsService.addGoods(goods);
        return AjaxResponse.success(goodsService.getGoodsByName(goods).getGoods_id());//返回id
    }

    /*
    * 删除商品
    * @Param goods_id
    * */
    @PostMapping("/deleteGoods")
    public @ResponseBody AjaxResponse deleteGoods(@RequestBody Goods goods){
        goodsService.deleteGoods(goods);
        return AjaxResponse.success(true);
    }

    /*
    * 修改商品信息
    * @Param goods_name/description  update_person
    * */
    @PostMapping("/updateGoods")
    public @ResponseBody AjaxResponse updateGoods(@RequestBody Goods goods){
        Date date = new Date();
        goods.setUpdate_time(date);
        goodsService.updateGoodsInfo(goods);
        return AjaxResponse.success(true);
    }

    /*
    * 获取全部的商品
    * @Param null
    * */
    @PostMapping("/getAllGoods")
    public @ResponseBody AjaxResponse getAllGoods(@RequestBody Goods goods){
        List<Goods> list = goodsService.getAllGoods();
        if(list.size()==0)  return AjaxResponse.fail("没有商品");
        return AjaxResponse.success(list);
    }
}
