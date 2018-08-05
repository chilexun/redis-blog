package com.github.blog.dao.impl;

import com.alibaba.fastjson.JSON;
import com.github.blog.configuration.RedisSupport;
import com.github.blog.dao.ArticleDao;
import com.github.blog.dao.SequenceSupport;
import com.github.blog.dto.ArticleDto;
import com.github.blog.utils.BeanUtils;
import com.github.blog.utils.HtmlUtils;
import com.github.blog.utils.NumberUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class ArticleDaoImpl implements ArticleDao {
	private final Logger log = LoggerFactory.getLogger(ArticleDao.class);

    private static final String ARTICLE_SEQ = "article:nextval";

    @Autowired
    private SequenceSupport seqSupport;
    @Autowired
    private RedisSupport redisSupport;

    @Override
    public void create(ArticleDto article) {
        article.setId(seqSupport.nextValue(ARTICLE_SEQ));

        if(article.getStatus() == null)
            article.setStatus(0);
        java.util.Date now = new java.util.Date();
        article.setCreated(now);
        article.setModified(now);
        if(article.getAllowComment()==null)
            article.setAllowComment(true);
        if(article.getAllowShare()==null)
            article.setAllowShare(false);

        String content = article.getContent();
        String header = StringUtils.left(HtmlUtils.getBodyText(content), 64);
        article.setContent(header);

        SessionCallback<Void> sessionCallback = new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisOperations.opsForHash().putAll((K)("article:" + article.getId()), BeanUtils.beanToMap(article, "userLike"));
                redisOperations.opsForValue().set((K)("article:content:" + article.getId()), (V)content);
                String score = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                redisOperations.opsForZSet().add((K)"article:ids", (V)article.getId(), Double.parseDouble(score));
                return null;
            }
        };
        redisSupport.executePipelined(sessionCallback);

        log.debug("Save activle article:{} success", JSON.toJSONString(article));
    }

    @Override
    public boolean update(ArticleDto article) {
        Assert.notNull(article.getId(),"id must not be null");
        boolean hasKey = redisSupport.hasKey("article:"+article.getId());
        if(!hasKey)
            return false;

        java.util.Date now = new java.util.Date();
        article.setModified(now);

        String content = article.getContent();
        if(StringUtils.isNotEmpty(content)) {
            String header = StringUtils.left(HtmlUtils.getBodyText(content), 64);
            article.setContent(header);
            redisSupport.set("article:content:"+article.getId(), content);
        }
        redisSupport.hmset("article:"+article.getId(),
                BeanUtils.beanToMap(article,"created","type","readCount","likeCount","commentCount","createUserId","createUserName","status"));

        return true;
    }

    @Override
    public ArticleDto getSummary(Long id) {
        Assert.notNull(id, "id must not be null");
        Map<Object,Object> result = redisSupport.hmget("article:"+id);
        if(MapUtils.isNotEmpty(result)){
            ArticleDto article = BeanUtils.mapToBean(result, ArticleDto.class);
            if(NumberUtils.zeroOnNull(article.getStatus()) <= 1)
                return article;
        }
        return null;
    }

    @Override
    public ArticleDto getDetail(Long id){
        ArticleDto article = this.getSummary(id);
        if(article != null) {
            String content = redisSupport.get("article:content:" + id).toString();
            if (StringUtils.isNotBlank(content))
                article.setContent(content);
            return article;
        }
        return null;
    }

    @Override
    public Integer increaseReadNum(Long id) {
        Assert.notNull(id, "id must not be null");
        return (int)redisSupport.hincr("article:"+id,"readCount", 1);
    }

    @Override
    public Integer increaseLikeNum(Long id) {
        Assert.notNull(id, "id must not be null");
        return (int)redisSupport.hincr("article:"+id,"likeCount", 1);
    }

    @Override
    public Integer decreaseLikeNum(Long id) {
        Assert.notNull(id, "id must not be null");
        return (int)redisSupport.hdecr("article:"+id,"likeCount", 1);
    }

    @Override
    public Integer increaseCommentNum(Long id) {
        Assert.notNull(id, "id must not be null");
        return (int)redisSupport.hincr("article:"+id,"commentCount", 1);
    }

    @Override
    public Integer decreaseCommentNum(Long id) {
        Assert.notNull(id, "id must not be null");
        return (int)redisSupport.hdecr("article:"+id,"commentCount", 1);
    }

    @Override
    public void updatePubStatus(ArticleDto article){
        Assert.notNull(article.getId(), "id must not be null");
        Assert.notNull(article.getStatus(), "status must not be null");

        ArticleDto newDto = new ArticleDto();
        java.util.Date now = Calendar.getInstance().getTime();
        newDto.setStatus(article.getStatus());
        if(article.getStatus() == 1)
            newDto.setIssueTime(now);
        newDto.setModified(now);
        redisSupport.hmset("article:"+article.getId(), BeanUtils.beanToMap(newDto));

        if(article.getStatus()==1) {
            String score = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            if(BooleanUtils.isTrue(article.getIstop()))
                score = "10"+score;
            redisSupport.zAdd("article:pub:ids", article.getId(), Double.parseDouble(score));
        }else if(article.getStatus()==0) {
            redisSupport.zRem("article:pub:ids", article.getId());
        }
    }

    @Override
    public void updateTopStatus(Long id, boolean isTop) {
        Assert.notNull(id, "id must not be null");

        Map<String, Object> propMap = new HashMap<>();
        propMap.put("modified", new java.util.Date());
        propMap.put("istop", isTop);
        redisSupport.hmset("article:"+id, propMap);
        Double score =  redisSupport.zScore("article:pub:ids", id);
        if(score != null){
            String newScore = String.valueOf(score.longValue());
            if(isTop)
                newScore = "10" + StringUtils.right(newScore, 14);
            else
                newScore = StringUtils.right(newScore, 14);
            redisSupport.zAdd("article:pub:ids", id, Double.parseDouble(newScore));
        }
    }

    @Override
    public boolean delete(final Long id) {
        Assert.notNull(id, "id must not be null");
        boolean hasKey = redisSupport.hasKey("article:"+id);
        if(hasKey) {
            SessionCallback<Void> sessionCallback = new SessionCallback<Void>() {
                @Override
                public <K, V> Void execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                    redisOperations.opsForZSet().remove((K)"article:pub:ids", id);
                    redisOperations.opsForZSet().remove((K)"article:ids", id);
                    redisOperations.delete((K) ("article:content:" + id));
                    redisOperations.opsForHash().put( (K)("article:" + id), "status", 2);
                    return null;
                }
            };
            redisSupport.executePipelined(sessionCallback);
            return true;
        }
        return false;
    }

    @Override
    public List<ArticleDto> list(int startIndex, int pageSize) {
        int stopIndex = startIndex+pageSize-1;
        Set<Object> ids = redisSupport.zRevRange("article:ids", startIndex, stopIndex);
        List<ArticleDto> articleList =
                ids.stream()
                    .map(e -> {
                        Map<Object,Object> result = redisSupport.hmget("article:" + e);
                        return BeanUtils.mapToBean(result, ArticleDto.class);})
                    .filter(e->(e!=null) && e.getId()!=null && NumberUtils.zeroOnNull(e.getStatus())<=1)
                    .sorted(ArticleDto::compareByCreated)
                    .collect(Collectors.toList());
        return articleList;
    }

    @Override
    public List<ArticleDto> listPub(int startIndex, int pageSize) {
        int stopIndex = startIndex+pageSize-1;
        Set<Object> ids = redisSupport.zRevRange("article:pub:ids", startIndex, stopIndex);
        List<ArticleDto> articleList =
                ids.stream()
                    .map(e -> {
                        Map<Object,Object> result = redisSupport.hmget("article:" + e);
                        return BeanUtils.mapToBean(result, ArticleDto.class);})
                    .filter(e->(e!=null) && e.getId()!=null && NumberUtils.zeroOnNull(e.getStatus())==1)
                    .sorted(ArticleDto::compareByIssueTime)
                    .collect(Collectors.toList());
        return articleList;
    }

}
