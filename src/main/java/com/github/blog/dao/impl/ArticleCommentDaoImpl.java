package com.github.blog.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.github.blog.configuration.RedisSupport;
import com.github.blog.dao.ArticleCommentDao;
import com.github.blog.dao.SequenceSupport;
import com.github.blog.dto.ArticleCommentDto;
import com.github.blog.utils.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ArticleCommentDaoImpl implements ArticleCommentDao {
    private static final String ARTICLE_COMMENT_SEQ = "comment:nextval";

    @Autowired
    private SequenceSupport seqSupport;
    @Autowired
    private RedisSupport redisSupport;

    @Override
    public void create(ArticleCommentDto commentDto) {
        Assert.notNull(commentDto.getArticleId(),"article id must not be null");
        Assert.notNull(commentDto.getUserId(),"user id must not be null");

        commentDto.setId(seqSupport.nextValue(ARTICLE_COMMENT_SEQ));
        Date now = Calendar.getInstance().getTime();
        commentDto.setCreated(now);
        commentDto.setModified(now);
        commentDto.setStatus(0);
        redisSupport.hmset("comment:"+commentDto.getId(), BeanUtils.beanToMap(commentDto, "userLike"));
        redisSupport.lPush("comment:ids:"+commentDto.getArticleId(), commentDto.getId());
    }

    @Override
    public boolean delete(Long commentId) {
        Assert.notNull(commentId, "comment id must not be null");

        Object articleId = redisSupport.hget("comment:"+commentId, "articleId");
        if(articleId == null)
            return false;
        redisSupport.lRemove("comment:ids:"+articleId, 1L,commentId);
        redisSupport.del("comment:"+commentId);
        return true;
    }

    @Override
    public List<ArticleCommentDto> list(Long articleId, int startIndex, int count) {
        int stopIndex = startIndex + count - 1;
        List<Object> commentList = redisSupport.lRange("comment:ids:"+articleId, startIndex, stopIndex);
        return commentList.stream()
                .map(e->{
                    Map<Object, Object> entity = redisSupport.hmget("comment:"+e);
                    return BeanUtils.mapToBean(entity, ArticleCommentDto.class); })
                .collect(Collectors.toList());
    }

    @Override
    public boolean exist(Long commentId) {
        return redisSupport.exists("comment:"+commentId);
    }

    @Override
    public int addLike(Long commentId, Long userId) {
        long num = redisSupport.sAdd("comment:like:"+userId, commentId );
        if(num > 0)
            num = (int)redisSupport.hincr("comment:"+commentId, "likeNum", 1);
        else
            num = (int)redisSupport.hget("comment:"+commentId, "likeNum");
        return (int)num;
    }

    @Override
    public int cancelLike(Long commentId, Long userId) {
        long num = redisSupport.sRemove("comment:like:"+userId, commentId);
        if(num > 0)
            num = (int)redisSupport.hdecr("comment:"+commentId, "likeNum", 1);
        else
            num = (int)redisSupport.hget("comment:"+commentId, "likeNum");
        return (int)num;
    }

    @Override
    public Set<Long> listLikes(Long userId) {
        Set<Object> commentSet = redisSupport.sMembers("comment:like:"+userId);
        return commentSet.stream()
                .map(e->(e instanceof Long)? (Long)e : Long.valueOf(e.toString()))
                .collect(Collectors.toSet());
    }
}
