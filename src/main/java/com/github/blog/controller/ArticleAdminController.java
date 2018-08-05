package com.github.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.blog.dto.ArticleDto;
import com.github.blog.dto.ValidGroups;
import com.github.blog.service.ArticleService;
import com.github.blog.utils.PageInfo;
import com.github.blog.utils.PageQuery;
import com.github.blog.utils.Response;
import com.github.blog.utils.ResultCode;

import javax.validation.Valid;
import javax.validation.groups.Default;

@RestController
@RequestMapping("/admin/article")
public class ArticleAdminController {

    private ArticleService articleService;

    @Autowired
    public ArticleAdminController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public Response<ArticleDto> load(@PathVariable Long id){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");

        Response<ArticleDto> result = articleService.findById(id);
        if(result.getCode() > 0)
            return new Response<>(result.getCode(),  result.getMessage());
        else if(result.getData() == null)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");

        return result;
    }

    @PostMapping("/add")
    public Response<Void> add(@RequestBody @Validated({ValidGroups.AddGroup.class,Default.class}) ArticleDto article, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new Response<>(ResultCode.INVALID_PARAM, bindingResult.getAllErrors().get(0).getDefaultMessage());

        Response<ArticleDto> response = articleService.create(article);
        if(response.getCode() > 0)
            return new Response<>(response.getCode(), response.getMessage());
        return new Response<>();
    }

    @PostMapping("/{id}/del")
    public Response<Void> delete(@PathVariable Long id){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");
        return articleService.delete(id);
    }

    @PostMapping("/{id}/edit")
    public Response<Void> edit(@PathVariable Long id, @RequestBody @Valid ArticleDto article, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new Response<>(ResultCode.INVALID_PARAM, bindingResult.getAllErrors().get(0).getDefaultMessage());

        article.setId(id);
        return articleService.update(article);
    }

    @PostMapping("/{id}/pub")
    public Response<Void> publish(@PathVariable Long id){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");
        return articleService.publish(id);
    }

    @PostMapping("/{id}/retract")
    public Response<Void> retract(@PathVariable Long id){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");
        return articleService.retract(id);
    }

    @GetMapping("/list")
    public Response<PageInfo<ArticleDto>> list(@RequestParam(value = "pageNo",required = false, defaultValue = "1") Integer pageNo,
                                               @RequestParam(value = "count",required = false, defaultValue = "10") Integer count){
        if(count!=null && (count<1 || count>50))
            return new Response<>(ResultCode.INVALID_PARAM,"分页数量必须在1-50之间");
        if(pageNo < 0) pageNo = 1;
        
        PageQuery pageQuery = new PageQuery(pageNo, count);
        Response<PageInfo<ArticleDto>> articles = articleService.list(pageQuery);
        return articles;
    }

    @PostMapping("/{id}/top")
    public Response<Void> top(@PathVariable Long id){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");
        return articleService.top(id);
    }

    @PostMapping("/{id}/unTop")
    public Response<Void> unTop(@PathVariable Long id){
        if(id <= 0)
            return new Response<>(ResultCode.INVALID_PARAM,"文章不存在");
        return articleService.unTop(id);
    }
}
