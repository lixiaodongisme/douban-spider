package com.lixiaodongisme.douban_spider.dao;

import com.lixiaodongisme.douban_spider.entity.Movie;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpiderDao {
   int insert(Movie movie);
}
