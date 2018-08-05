package com.github.blog.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.blog.configuration.RedisSupport;
import com.github.blog.dao.ArticleMessageDao;

@Repository
public class ArticleMessageDaoImpl implements ArticleMessageDao {
	private static final String article_msg_queue = "article:msg";
	
	@Autowired
    private RedisSupport redisSupport;
	
	@Override
	public void push(Long articleId) {
		redisSupport.lPush(article_msg_queue, articleId);
	}

	@Override
	public Long take(long timeout) {
		return (Long)redisSupport.bRPop(article_msg_queue, timeout);
	}

}
