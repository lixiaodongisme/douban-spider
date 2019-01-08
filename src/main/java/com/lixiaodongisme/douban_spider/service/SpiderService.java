package com.lixiaodongisme.douban_spider.service;

import com.lixiaodongisme.douban_spider.config.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:properties/config.properties")
@Service
public class SpiderService {

    @Autowired
    private RequestHeader header;

    public String test() {
        return header.getUserAgents();
    }
}
