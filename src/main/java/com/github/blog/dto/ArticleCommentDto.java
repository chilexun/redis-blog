package com.github.blog.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 文章评论实体类
 */
public class ArticleCommentDto  implements java.io.Serializable{
	private static final long serialVersionUID = 1108332274455221612L;
	
	private Long id;
    private Long articleId;
    private Long userId;
    private String userName;
    private Date created;  //发表评论的时间
    private Date modified; //最后修改时间
    private Integer status; //0:有效, 1:已删除
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 512, message ="评论字数不能超过512个")
    private String content;
    private Integer likeNum;
    private boolean userLike; //仅前端展示用，当前用户是否已点赞

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }
    public void setUserLike(boolean userLike){
        this.userLike = userLike;
    }
    public boolean getUserLike() {
        return userLike;
    }
}
