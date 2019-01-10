package com.lixiaodongisme.douban_spider.controller;

import com.lixiaodongisme.douban_spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {
   @Autowired
   private SpiderService spiderService;

   @RequestMapping("/1")
   public void test() {
      spiderService.spiderDouBan();
   }
}
