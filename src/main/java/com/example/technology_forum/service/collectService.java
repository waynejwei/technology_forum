package com.example.technology_forum.service;

import com.example.technology_forum.model.AjaxResponse;
import com.example.technology_forum.model.Collection;
import com.example.technology_forum.model.Favorites;

import java.util.List;

public interface collectService {

    /*收藏夹功能*/
    AjaxResponse createFavorites(Favorites favorites);

    AjaxResponse deleteFavorites(Favorites favorites);

    AjaxResponse updateFavoritesName(Favorites favorites);

    Favorites getFavoritesId(Favorites favorites);

    List<Favorites> getFavorites(Favorites favorites);


    /*收藏功能*/
    AjaxResponse collectObject(Collection collection);

    void deleteCollection(Collection collection);

    AjaxResponse updateCollection(Collection collection);

    List<Collection> getFavoritesCollection(Collection collection);

//    Collection getCollectionId(Collection collection);

    Collection getCollectionById(Collection collection);

}
