package com.example.technology_forum.service;

import com.example.technology_forum.model.Special;

import java.util.List;

public interface specialService {

    void followPerson(Special special);

    void cancelFollowing(Special special);

    int countFollowers(Special special);

    int countFans(Special special);

    List<Special> getFollowers(Special special);

    List<Special> getFans(Special special);

    Special hasFollowed(Special special);
}
