package com.github.blog.service.impl;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.github.blog.dto.ArticleDto;
import com.github.blog.service.ArticleService;
import com.github.blog.utils.Response;

import org.junit.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ArticleServiceImplTest {

	@Autowired
	private ArticleService articleService;
	@Test
	public void testCreateArticle() {
		ArticleDto article = new ArticleDto();
		article.setAuthor("HR");
    	article.setTitle("2018新员工培训");
    	article.setContent("<p>开始啦</p>");
    	Response<ArticleDto> response = articleService.create(article);
		System.out.println(JSON.toJSONString(response));
	}
}