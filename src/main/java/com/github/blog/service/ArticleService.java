package com.github.blog.service;

import java.util.Set;

import com.github.blog.dto.ArticleCommentDto;
import com.github.blog.dto.ArticleDto;
import com.github.blog.utils.PageInfo;
import com.github.blog.utils.PageQuery;
import com.github.blog.utils.Response;

public interface ArticleService {
	/**
	 * 根据ID获取文章属性，不包含文章详细内容
	 * @param id
	 * @return
	 */
	Response<ArticleDto> getSummary(Long id);
	/**
	 * 根据ID获取文章详情，包含文章内容
	 * @param id
	 * @return
	 */
	Response<ArticleDto> findById(Long id);
	/**
	 * 功能同findById，使用该接口会使文章的阅读数+1
	 * @param id
	 * @return
	 */
	Response<ArticleDto> read(Long id);

	Response<ArticleDto> create(ArticleDto article);

	Response<Void> delete(Long id);

	Response<Void> update(ArticleDto article);

	/**
	 * 发布文章
	 * @param id
	 */
	Response<Void> publish(Long id);

	/**
	 * 撤回文章
	 * @param id
	 */
	Response<Void> retract(Long id);

	/**
	 * 置顶文章
	 * @param id
	 * @return
	 */
	Response<Void> top(Long id);

	/**
	 * 取消置顶文章
	 * @param id
	 * @return
	 */
	Response<Void> unTop(Long id);

	/**
	 * 分页获取所有文章，包含未发布和已发布的
	 * @param page
	 * @return
	 */
	Response<PageInfo<ArticleDto>> list(PageQuery page);

	/**
	 * 分页获取指定类型的文章，只会返回已发布的
	 * @param type
	 * @param page
	 * @return
	 */
	Response<PageInfo<ArticleDto>> listPub(PageQuery page);

	/**
	 * 点赞
	 * @param articleId 文章id
	 * @param userId 点赞用户id
	 * @return 当前点赞人数
	 */
	Response<Integer> addLike(Long articleId, Long userId);

	/**
	 * 取消赞
	 * @param articleId
	 * @param userId
	 * @return 当前点赞人数
	 */
	Response<Integer> cancelLike(Long articleId, Long userId);

	/**
	 * 获取用户点赞状态
	 * @param articleId
	 * @param userId
	 * @return
	 */
	Response<Boolean> getUserLikeStatus(Long articleId, Long userId);

	/**
	 * 发表评论
	 * @param comment
	 * @return
	 */
	Response<ArticleCommentDto> addComment(ArticleCommentDto comment);

	/**
	 * 删除评论
	 * @return
	 */
	Response<Void> deleteComment(Long articleId, Long commentId);

	/**
	 * 查询评论列表，按时间倒序排列
	 * @param page
	 * @return
	 */
	Response<PageInfo<ArticleCommentDto>> listComments(Long articleId, PageQuery page);

	/**
	 * 点赞
	 * @param commentId 评论id
	 * @param userId 点赞用户id
	 * @return 当前点赞人数
	 */
	Response<Integer> addLikeComment(Long commentId, Long userId);

	/**
	 * 取消赞
	 * @param commentId
	 * @param userId
	 * @return 当前点赞人数
	 */
	Response<Integer> cancelLikeComment(Long commentId, Long userId);

	/**
	 * 获取用户点赞过的评论ID
	 * @param userId
	 * @return 评论ID集合
	 */
	Response<Set<Long>> getUserLikeComments(Long userId);
}
