package com.github.blog.dao;

import java.util.List;

import com.github.blog.dto.ArticleDto;

public interface ArticleDao {

    void create(ArticleDto article);

    boolean update(ArticleDto article);

    /**
     * 获取文章的概要信息
     */
    ArticleDto getSummary(Long id);

    /**
     * 获取文章详情
     */
    ArticleDto getDetail(Long id);

    /**
     * 阅读人数+1
     * @param id
     * @return 返回当前阅读人数
     */
    Integer increaseReadNum(Long id);

    boolean delete(Long id);

    List<ArticleDto> list(int pageNo, int pageSize);

    List<ArticleDto> listPub(int pageNo, int pageSize);

    /**
     * 点赞人数+1
     * @param id
     * @return 返回当前点赞人数
     */
    Integer increaseLikeNum(Long id);

    /**
     * 点赞人数-1
     * @param id
     * @return 返回当前点赞人数
     */
    Integer decreaseLikeNum(Long id);

    /**
     * 评论人数+1
     * @param id
     */
    Integer increaseCommentNum(Long id);

    /**
     * 评论人数-1
     * @param id
     * @return 当前评论数
     */
    Integer decreaseCommentNum(Long id);

    /**
     * 修改发布状态
     * @param article
     */
    void updatePubStatus(ArticleDto article);
    /**
     * 修改置顶状态
     * @param id
     * @param isTop true:置顶, false:未置顶
     */
    void updateTopStatus(Long id, boolean isTop);
}
