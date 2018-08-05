package com.github.blog.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.blog.configuration.RedisSupport;
import com.github.blog.dao.SequenceSupport;

@Repository
public class RedisSequenceImpl implements SequenceSupport {
    @Autowired
    private RedisSupport redisSupport;

    @Override
    public Long nextValue(String sequenceName) {
        return redisSupport.incr(sequenceName, 1);
    }
}
