package com.lixiaodongisme.douban_spider.dao;

import com.lixiaodongisme.douban_spider.entity.Movie;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpiderDao {
   int insert(List<Movie> movie);
}
