package com.example.technology_forum.service;

import com.example.technology_forum.dao.specialDao;
import com.example.technology_forum.model.Special;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class specialJDBCService implements specialService {

    @Resource
    specialDao specialDao;

    @Override
    public void followPerson(Special special) {
        specialDao.followPerson(special);
    }

    @Override
    public void cancelFollowing(Special special) {
        specialDao.cancelFollowing(special);
    }

    @Override
    public int countFollowers(Special special) {
        List<Special> list = specialDao.getFollowers(special);
        return list.size();
    }

    @Override
    public int countFans(Special special) {
        List<Special> list = specialDao.getFans(special);
        return list.size();
    }

    @Override
    public Special hasFollowed(Special special) {
        try{
            return specialDao.hasFollowed(special);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<Special> getFollowers(Special special) {
        return specialDao.getFollowers(special);
    }

    @Override
    public List<Special> getFans(Special special) {
        return specialDao.getFans(special);
    }

}
