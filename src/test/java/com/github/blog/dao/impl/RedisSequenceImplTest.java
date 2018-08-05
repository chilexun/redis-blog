package com.github.blog.dao.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.blog.dao.SequenceSupport;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisSequenceImplTest {

    @Autowired
    private SequenceSupport redisSeqSupport;

    @Test
    public void nextValue() {
        long id =redisSeqSupport.nextValue("article:nextval");
        System.out.println(id);
    }
}