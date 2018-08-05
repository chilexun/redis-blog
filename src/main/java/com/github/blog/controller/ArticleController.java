package com.github.blog.controller;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.blog.annotation.SessionAttr;
import com.github.blog.dto.ArticleDto;
import com.github.blog.dto.UserDto;
import com.github.blog.service.ArticleService;
import com.github.blog.utils.NumberUtils;
import com.github.blog.utils.PageInfo;
import com.github.blog.utils.PageQuery;
import com.github.blog.utils.Response;
import com.github.blog.utils.ResultCode;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private ArticleService articleService;

    public ArticleController(@Autowired ArticleService articleService) {
    	this.articleService = articleService;
    }

    @GetMapping("/list")
    public Response<PageInfo<ArticleDto>> list(@RequestParam(value = "pageNo",required = false, defaultValue = "1") Integer pageNo,
                                       @RequestParam(value = "count",required = false, defaultValue = "10") Integer count){
        if(count!=null && (count<1 || count>50))
            return new Response<>(ResultCode.INVALID_PARAM,"分页数量必须在1-50之间");
        if(pageNo < 0) pageNo = 1;
        PageQuery pageQuery = new PageQuery(pageNo, count);
        Response<PageInfo<ArticleDto>> articles = articleService.listPub(pageQuery);
        return articles;
    }

    @GetMapping("/{id}")
    public Response<ArticleDto> read(@PathVariable Long id, @SessionAttr("user") UserDto user){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");
        if(user==null || NumberUtils.zeroOnNull(user.getUserId())<=0){
            Response<ArticleDto> summary = articleService.getSummary(id);
            if(summary.getData()==null || BooleanUtils.isNotTrue(summary.getData().getAllowShare()))
                return new Response<>(ResultCode.ACL_FAIL,"需登录后才能查看");
        }

        Response<ArticleDto> result = articleService.read(id);
        if(result.getCode() > 0)
            return new Response<>(result.getCode(),  result.getMessage());
        else if(result.getData() == null)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");

        if(user!=null && NumberUtils.zeroOnNull(user.getUserId())>0) {
            Response<Boolean> likeStatus = articleService.getUserLikeStatus(id, user.getUserId());
            if (BooleanUtils.isNotTrue(likeStatus.getData()))
                result.getData().setUserLike(false);
            else
                result.getData().setUserLike(true);
        }

        return result;
    }

    @PostMapping("/{articleId}/like")
    public Response<Integer> like(@PathVariable Long articleId,@SessionAttr("user") UserDto user){
        if(articleId < 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");

        return articleService.addLike(articleId, user.getUserId());
    }

    @PostMapping("/{articleId}/unlike")
    public Response<Integer> unlike(@PathVariable Long articleId,@SessionAttr("user") UserDto user){
        if(articleId < 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");

        return articleService.cancelLike(articleId, user.getUserId());
    }

}
