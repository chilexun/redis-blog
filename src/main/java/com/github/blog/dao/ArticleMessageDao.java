package com.github.blog.dao;

public interface ArticleMessageDao {
    /**
     * 发送一个article变化消息
     * @param articleId
     */
	void push(Long articleId);
	
	/**
	 * 获取一个article变化消息,如果没有消息，则等待
	 */
	Long take(long timeout);
}
