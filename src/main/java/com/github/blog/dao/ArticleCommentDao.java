package com.github.blog.dao;

import java.util.List;
import java.util.Set;

import com.github.blog.dto.ArticleCommentDto;

public interface ArticleCommentDao {

    void create(ArticleCommentDto commentDto);

    boolean delete(Long commentId);

    /**
     * 获取指定数量comments, 按创建时间倒序排列
     * @param articleId 文章id
     * @param startIndex 起始offset
     * @param count 数量
     * @return
     */
    List<ArticleCommentDto> list(Long articleId, int startIndex, int count);

    /**
     * 查询id指定的comment是否存在
     * @param commentId
     * @return
     */
    boolean exist(Long commentId);

    /**
     * 用户给comment点赞
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 评论赞个数
     */
    int addLike(Long commentId, Long userId);

    /**
     * 用户对评论取消赞
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 评论赞个数
     */
    int cancelLike(Long commentId, Long userId);

    /**
     * 获取用户点过赞的评论
     * @return 点过赞的评论列表Set
     */
    Set<Long> listLikes(Long userId);
}
