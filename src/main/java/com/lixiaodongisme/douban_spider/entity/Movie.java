package com.lixiaodongisme.douban_spider.entity;

import java.util.List;
import java.util.Objects;

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

   @Override
   public boolean equals(Object o) {
      if(this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;
      Movie movie = (Movie) o;
      return Objects.equals(id, movie.id) && Objects.equals(title, movie
              .title) && Objects.equals(rate, movie.rate) && Objects.equals
              (cover, movie.cover) && Objects.equals(url, movie.url) &&
              Objects.equals(directors, movie.directors) && Objects.equals
              (casts, movie.casts);
   }

   @Override
   public int hashCode() {

      return Objects.hash(id, title, rate, cover, url, directors, casts);
   }
}
