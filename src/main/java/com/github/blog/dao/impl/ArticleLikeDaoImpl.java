package com.github.blog.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.github.blog.configuration.RedisSupport;
import com.github.blog.dao.ArticleLikeDao;

@Repository
public class ArticleLikeDaoImpl implements ArticleLikeDao {

    @Autowired
    private RedisSupport redisSupport;

    @Override
    public boolean add(Long articleId, Long userId) {
        Assert.notNull(articleId, "article id must not be null");
        Assert.notNull(userId,"user id must not be null");

        long num = redisSupport.sAdd("article:like:"+articleId, userId);
        return (num > 0);
    }

    @Override
    public boolean delete(Long articleId, Long userId) {
        Assert.notNull(articleId, "article id must not be null");
        Assert.notNull(userId,"user id must not be null");

        long num = redisSupport.sRemove("article:like:"+articleId,  userId);
        return (num > 0);
    }

    @Override
    public boolean getLikeStatus(Long articleId, Long userId) {
        Assert.notNull(articleId, "article id must not be null");
        Assert.notNull(userId, "user id must not be null");

        return redisSupport.sIsMember("article:like:"+articleId,  userId);
    }
}
