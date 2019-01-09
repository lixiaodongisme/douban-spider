package com.lixiaodongisme.douban_spider.entity;

import java.util.List;

public class Movie {
   private String id;

   private String title;

   private Double rate;

   private String cover;

   private String url;

   private String directors;

   private String casts;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Double getRate() {
      return rate;
   }

   public void setRate(Double rate) {
      if (rate == null) {
         rate = 0d;
      }
      this.rate = rate;
   }

   public String getCover() {
      return cover;
   }

   public void setCover(String cover) {
      this.cover = cover;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getDirectors() {
      return directors;
   }

   public void setDirectors(String directors) {
      this.directors = directors;
   }

   public String getCasts() {
      return casts;
   }

   public void setCasts(String casts) {
      this.casts = casts;
   }
}
