package com.github.blog.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.blog.dao.ArticleCommentDao;
import com.github.blog.dao.ArticleDao;
import com.github.blog.dao.ArticleLikeDao;
import com.github.blog.dao.ArticleMessageDao;
import com.github.blog.dto.ArticleCommentDto;
import com.github.blog.dto.ArticleDto;
import com.github.blog.service.ArticleService;
import com.github.blog.utils.HtmlUtils;
import com.github.blog.utils.NumberUtils;
import com.github.blog.utils.PageInfo;
import com.github.blog.utils.PageQuery;
import com.github.blog.utils.Response;
import com.github.blog.utils.ResultCode;

import java.util.List;
import java.util.Set;

@Service
public class ArticleServiceImpl implements ArticleService{
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private ArticleMessageDao messageDao;
	@Autowired
	private ArticleLikeDao articleLikeDao;
	@Autowired
	private ArticleCommentDao articleCommentDao;

	@Override
	public Response<ArticleDto> getSummary(Long id) {
		ArticleDto dto = articleDao.getSummary(id);
		return new Response<>(dto);
	}

	@Override
    public Response<ArticleDto> findById(Long articleId){
		ArticleDto dto = articleDao.getDetail(articleId);
		return new Response<>(dto);
    }

    @Override
	public Response<ArticleDto> read(Long id){
		ArticleDto dto = articleDao.getDetail(id);
		if(dto != null && dto.getStatus() == 1) {
			Integer current = articleDao.increaseReadNum(id);
			dto.setReadCount(current);
			return new Response<>(dto);
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已被删除");
	}

	@Override
	public Response<ArticleDto> create(ArticleDto article) {
		article.setContent(HtmlUtils.getSafeBody(article.getContent()));
		articleDao.create(article);
		if(article.getStatus().intValue() == 1)
			articleDao.updatePubStatus(article);
		messageDao.push(article.getId());
		return new Response<>(article);
	}

	@Override
	public Response<Void> delete(Long id) {
		boolean result = articleDao.delete(id);
		if(result)
			messageDao.push(id);
		return new Response<>();
	}

	@Override
	public Response<Void> update(ArticleDto article) {
		ArticleDto rawArticle = articleDao.getSummary(article.getId());
		if(rawArticle != null) {
			article.setContent(HtmlUtils.getSafeBody(article.getContent()));
			boolean result = articleDao.update(article);
			if (result) {
				boolean pubChanged = false;
				if(NumberUtils.zeroOnNull(article.getStatus())==1 ){
					if(NumberUtils.zeroOnNull(rawArticle.getStatus())==0) {
						ArticleDto newArticle = new ArticleDto();
						newArticle.setId(article.getId());
						newArticle.setStatus(1);
						newArticle.setIstop(article.getIstop() != null ? article.getIstop() : rawArticle.getIstop());
						articleDao.updatePubStatus(article);
						pubChanged = true;
					}
				}
				if(article.getIstop()!=null && !pubChanged
				       &&article.getIstop().booleanValue()!=BooleanUtils.toBooleanDefaultIfNull(rawArticle.getIstop(),false)){
					articleDao.updateTopStatus(article.getId(),article.getIstop());
				}

				messageDao.push(article.getId());
				return new Response<>();
			}
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已被删除");
	}

	@Override
	public Response<Void> publish(Long id) {
		ArticleDto rawArticle = articleDao.getSummary(id);
		if(rawArticle != null && NumberUtils.zeroOnNull(rawArticle.getStatus())==0) {
			ArticleDto newArticle = new ArticleDto();
			newArticle.setId(id);
			newArticle.setStatus(1);
			newArticle.setIstop(rawArticle.getIstop());
			articleDao.updatePubStatus(newArticle);

			messageDao.push(id);
			return new Response<>();
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已发布");
	}

	@Override
	public Response<Void> retract(Long id) {
		ArticleDto rawArticle = articleDao.getSummary(id);
		if(rawArticle != null && NumberUtils.zeroOnNull(rawArticle.getStatus())==1) {
			ArticleDto newArticle = new ArticleDto();
			newArticle.setId(id);
			newArticle.setStatus(0);
			articleDao.updatePubStatus(newArticle);

			messageDao.push(id);
			return new Response<>();
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者未发布");
	}

	@Override
	public Response<Void> top(Long id) {
		ArticleDto rawArticle = articleDao.getSummary(id);
		if(rawArticle == null)
			return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已被删除");

		if(BooleanUtils.isNotTrue(rawArticle.getIstop()))
		    articleDao.updateTopStatus(id, true);
		return new Response<>();
	}

	@Override
	public Response<Void> unTop(Long id) {
		ArticleDto rawArticle = articleDao.getSummary(id);
		if(rawArticle == null)
			return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已被删除");

		if(BooleanUtils.isTrue(rawArticle.getIstop()))
			articleDao.updateTopStatus(id, false);
		return new Response<>();
	}

	@Override
	public Response<PageInfo<ArticleDto>> list(PageQuery page){
		List<ArticleDto> articles = articleDao.list(page.getStartIndex(), page.getPageSize()+1);
		return new Response<>(new PageInfo<>(articles, page));
	}

	@Override
	public Response<PageInfo<ArticleDto>> listPub(PageQuery page) {
		List<ArticleDto> articles = articleDao.listPub(page.getStartIndex(), page.getPageSize()+1);
		return new Response<>(new PageInfo<>(articles, page));
	}

	@Override
	public Response<Integer> addLike(Long articleId, Long userId) {
		ArticleDto article = articleDao.getSummary(articleId);		
		if(article != null) {
			Integer likeCount = article.getLikeCount();
			boolean result = articleLikeDao.add(articleId, userId);
			if(result)
				likeCount = articleDao.increaseLikeNum(articleId);
			return new Response<>(likeCount==null ? 0:likeCount);
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已被删除");
	}

	@Override
	public Response<Integer> cancelLike(Long articleId, Long userId) {
		ArticleDto article = articleDao.getSummary(articleId);		
		if(article != null) {
			Integer likeCount = article.getLikeCount();
			boolean result = articleLikeDao.delete(articleId, userId);
			if(result)
				likeCount = articleDao.decreaseLikeNum(articleId);
			return new Response<>(likeCount==null ? 0:likeCount);
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "文章不存在或者已被删除");
	}

	@Override
	public Response<Boolean> getUserLikeStatus(Long articleId, Long userId) {
		boolean result = articleLikeDao.getLikeStatus(articleId, userId);
		return new Response<>(result);
	}

	@Override
	public Response<ArticleCommentDto> addComment(ArticleCommentDto comment) {
		comment.setContent(HtmlUtils.getBodyText(comment.getContent()));
		articleCommentDao.create(comment);
		if(comment.getId() > 0)
			articleDao.increaseCommentNum(comment.getArticleId());
		return new Response<>(comment);
	}

	@Override
	public Response<Void> deleteComment(Long articleId, Long commentId) {
		boolean result = articleCommentDao.delete(commentId);
		if(result) {
			articleDao.decreaseLikeNum(articleId);
			return new Response<>();
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "评论不存在或者已被删除");
	}

	@Override
	public Response<PageInfo<ArticleCommentDto>> listComments(Long articleId, PageQuery page) {
		List<ArticleCommentDto> commentList = articleCommentDao.list(articleId, page.getStartIndex(), page.getPageSize()+1);
		return new Response<>(new PageInfo<>(commentList, page));
	}

	@Override
	public Response<Integer> addLikeComment(Long commentId, Long userId) {
		boolean exist = articleCommentDao.exist(commentId);
		if(exist){
			int num = articleCommentDao.addLike(commentId, userId);
			return new Response<>(num);
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "评论不存在或者已被删除");
	}

	@Override
	public Response<Integer> cancelLikeComment(Long commentId, Long userId) {
		boolean exist = articleCommentDao.exist(commentId);
		if(exist){
			int num = articleCommentDao.cancelLike(commentId, userId);
			return new Response<>(num);
		}
		return new Response<>(ResultCode.LOGICAL_ERROR, "评论不存在或者已被删除");
	}

	@Override
	public Response<Set<Long>> getUserLikeComments(Long userId) {
		return new Response<>(articleCommentDao.listLikes(userId));
	}
}
