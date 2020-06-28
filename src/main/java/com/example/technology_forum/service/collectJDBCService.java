package com.example.technology_forum.service;

import com.example.technology_forum.dao.collectionDao;
import com.example.technology_forum.dao.favoritesDao;
import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Collection;
import com.example.technology_forum.model.Favorites;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class collectJDBCService implements collectService{

    @Resource
    favoritesDao favoritesDao;

    @Resource
    collectionDao collectionDao;

    /****************************************收藏夹的管理************************************************/
    @Override
    public AjaxResponse createFavorites(Favorites favorites) {
        try{
            Favorites userFavorites = favoritesDao.getFavoritesId(favorites);
            return AjaxResponse.fail("你已创建同名的收藏夹！收藏夹id为："+userFavorites.getFavorites_id());
        }catch (EmptyResultDataAccessException e){
            favoritesDao.createFavorites(favorites);
            return AjaxResponse.success(favoritesDao.getFavoritesId(favorites).getFavorites_id());
        }
    }

    @Override
    public AjaxResponse deleteFavorites(Favorites favorites) {
        favorites.setU_id(favoritesDao.getFavoritesById(favorites).getU_id());
        favorites.setFavorites_name(favoritesDao.getFavoritesById(favorites).getFavorites_name());
        if("我的收藏夹".equals(favorites.getFavorites_name()))  return AjaxResponse.fail("我的收藏夹不可删除！");
        Collection collection = new Collection();
        collection.setFavorites_id(favorites.getFavorites_id());
        List<Collection> collectionList = collectionDao.getFavoritesCollection(collection);//将被删除的收藏夹里的收藏列表
        /*获取我的收藏夹的收藏id*/
        Favorites favorites1 = new Favorites();
        favorites1.setU_id(favorites.getU_id());
        favorites1.setFavorites_name("我的收藏夹");
        Favorites myFavorites;
        try{
            myFavorites = favoritesDao.getFavoritesId(favorites1);
        }catch (EmptyResultDataAccessException e){
            return AjaxResponse.fail("我的收藏夹不存在！");
        }
        log.info("{collectionJDBCService-deleteFavorites-myFavoritesId}："+myFavorites.getFavorites_id());
        for (Collection collection1:collectionList){
            /*检查我的收藏夹里是否已经有同名的收藏了*/
            try {
                collection1.setFavorites_id(myFavorites.getFavorites_id());
                collectionDao.getCollectionId(collection1);//我的收藏夹下有没有同名收藏
                try{
                    collection1.setFavorites_id(myFavorites.getFavorites_id());
                    Collection collection2 = collectionDao.getSameCollection(collection1);//内容也一样的收藏,则将其丢弃
                    log.info("{collectJDBCService-deleteFavorites}:同内容收藏");
                    collectionDao.deleteCollection(collection1);//将这个重复的收藏也删除了
                }catch(EmptyResultDataAccessException e){//仅仅只是同名
                    collection1.setFavorites_id(myFavorites.getFavorites_id());
                    collection1.setCollection_name(String.valueOf(collection1.getCollection_time()));//如果仅仅是重名的话，则修改他的名字为收藏时间
//                    collectionDao.collectObject(collection1);
                    collectionDao.moveToMyFavorites(collection1);//将其移动至我的收藏夹
                    log.info("{collectJDBCService-deleteFavorites}:同名收藏");
                }
            }catch(EmptyResultDataAccessException e){//我的收藏夹里没有同名的收藏
                log.info("{collectJDBCService-deleteFavorites}:不同名也不同内容收藏");
                collection1.setFavorites_id(myFavorites.getFavorites_id());
//                collectionDao.collectObject(collection1);//将被删除收藏夹的收藏移到我的收藏夹里
                collectionDao.moveToMyFavorites(collection1);
            }
        }
        favoritesDao.deleteFavorites(favorites);
        return AjaxResponse.success("成功删除收藏夹，并且将收藏移至“我的收藏夹”下");
    }

    @Override
    public AjaxResponse updateFavoritesName(Favorites favorites) {
        if("我的收藏夹".equals(favoritesDao.getFavoritesById(favorites).getFavorites_name())) return AjaxResponse.fail("我的收藏夹不可修改！");
        try{
            Favorites userFavorites = favoritesDao.getFavoritesId(favorites);
            return AjaxResponse.fail("你已创建同名的收藏夹！收藏夹id为：" + userFavorites.getFavorites_id());
        }catch (EmptyResultDataAccessException e){
            favoritesDao.updateFavoritesName(favorites);
            return AjaxResponse.success(favoritesDao.getFavoritesId(favorites).getFavorites_id());
        }
    }

    @Override
    public Favorites getFavoritesId(Favorites favorites) {
        try{
            return favoritesDao.getFavoritesId(favorites);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<Favorites> getFavorites(Favorites favorites) {
        return favoritesDao.getFavorites(favorites);
    }


    /****************************************收藏的功能************************************************/


    @Override
    public AjaxResponse collectObject(Collection collection) {
        try{
            collectionDao.getCollectionId(collection);
            return AjaxResponse.fail("已经有同名的收藏！");
        }catch(EmptyResultDataAccessException e){
            Date date = new Date();
            collection.setCollection_time(date);
            collectionDao.collectObject(collection);
            return AjaxResponse.success(collectionDao.getCollectionId(collection).getCollection_id());
        }
    }

    @Override
    public void deleteCollection(Collection collection) {
        collectionDao.deleteCollection(collection);
    }

    @Override
    public AjaxResponse updateCollection(Collection collection) {
            collectionDao.updateCollection(collection);
            return AjaxResponse.success(true);
    }

    @Override
    public List<Collection> getFavoritesCollection(Collection collection) {
        return collectionDao.getFavoritesCollection(collection);
    }

//    @Override
//    public Collection getCollectionId(Collection collection) {
//        try{
//            return collectionDao.getCollectionId(collection);
//        }catch (EmptyResultDataAccessException e){
//            return null;
//        }
//    }

    @Override
    public Collection getCollectionById(Collection collection) {
        try{
            return collectionDao.getCollectionById(collection);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }


}
