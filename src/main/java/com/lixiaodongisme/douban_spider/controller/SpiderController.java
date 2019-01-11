package com.lixiaodongisme.douban_spider.controller;

import com.lixiaodongisme.douban_spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spider")
public class SpiderController {
   @Autowired
   private SpiderService spiderService;

   @RequestMapping("/spiderMovie")
   public void spiderAllMovie() {
      spiderService.spiderMovie();
   }
}
