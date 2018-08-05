package com.github.blog.dao;

public interface ArticleLikeDao {
    /**
     * 添加点赞人
     * @param articleId
     * @param userId
     * @return true:添加成功; false:之前已添加
     */
    boolean add(Long articleId, Long userId);

    /**
     * 删除点赞人
     * @param articleId
     * @param userId
     * @return true:取消未成功; false:之前未添加
     */
    boolean delete(Long articleId, Long userId);

    /**
     * 获取用户是否点赞
     * @param articleId
     * @param userId
     * @return
     */
    boolean getLikeStatus(Long articleId, Long userId);
}
