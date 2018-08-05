package com.github.blog.controller;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.github.blog.annotation.SessionAttr;
import com.github.blog.dto.ArticleCommentDto;
import com.github.blog.dto.ArticleDto;
import com.github.blog.dto.UserDto;
import com.github.blog.service.ArticleService;
import com.github.blog.utils.PageInfo;
import com.github.blog.utils.PageQuery;
import com.github.blog.utils.Response;
import com.github.blog.utils.ResultCode;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/article/{articleId}/comment")
public class ArticleCommentController {
    private ArticleService articleService;

    public ArticleCommentController(@Autowired ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping("/list")
    public Response<PageInfo<ArticleCommentDto>> list(@PathVariable Long articleId,
                                                      @RequestParam(value = "pageNo",required = false) Integer pageNo,
                                                      @RequestParam(value = "count",required = false,defaultValue = "10") Integer count){
        if(count!=null && (count<1 || count>50))
            return new Response<>(ResultCode.INVALID_PARAM,"分页数量必须在1-50之间");
        if(pageNo == null) pageNo = 1;

        PageQuery pageQuery = new PageQuery(pageNo, count);
        Response<PageInfo<ArticleCommentDto>> response = articleService.listComments(articleId, pageQuery);
        if(response.getData() != null){
            Long userId = 1L;
            Response<Set<Long>> userLikeList = articleService.getUserLikeComments(userId);
            if(userLikeList.getData() != null) {
                response.getData().getList().forEach(e -> {
                    if (userLikeList.getData().contains(e.getId()))
                        e.setUserLike(true);
                });
            }
        }
        return response;
    }

    @PostMapping("/add")
    public Response<ArticleCommentDto> add(@PathVariable Long articleId, @RequestBody @Valid ArticleCommentDto comment,@SessionAttr("user") UserDto user, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new Response<>(ResultCode.INVALID_PARAM, bindingResult.getAllErrors().get(0).getDefaultMessage());
        Response<ArticleDto> response = articleService.getSummary(articleId);
        if(response.getCode() > 0)
            return new Response<>(ResultCode.LOGICAL_ERROR,response.getMessage());
        if(response.getData() == null)
            return new Response<>(ResultCode.LOGICAL_ERROR,"文章不存在或者已被删除");
        if(BooleanUtils.isFalse(response.getData().getAllowComment()))
            return new Response<>(ResultCode.LOGICAL_ERROR,"文章不允许评论");

        comment.setArticleId(articleId);
        comment.setUserId(user.getUserId());
        comment.setUserName(user.getUsername());
        return articleService.addComment(comment);
    }

    @PostMapping("/{id}/del")
    public Response<Void> delete(@PathVariable Long articleId, @PathVariable Long id){
        return articleService.deleteComment(articleId, id);
    }

    @PostMapping("/{id}/like")
    public Response<Integer> like(@PathVariable Long articleId, @PathVariable Long id,@SessionAttr("user") UserDto user){
        return articleService.addLikeComment(id, user.getUserId());
    }

    @PostMapping("/{id}/unlike")
    public Response<Integer> unlike(@PathVariable Long articleId, @PathVariable Long id, @SessionAttr("user") UserDto user){
        return articleService.cancelLikeComment(id, user.getUserId());
    }
}
