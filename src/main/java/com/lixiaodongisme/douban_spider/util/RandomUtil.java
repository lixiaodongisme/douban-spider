package com.lixiaodongisme.douban_spider.util;

import java.util.Random;

public class RandomUtil {
   private RandomUtil() {

   }

   /*
   * 获取固定长度的随机字符串，伪造豆瓣Cookie的bid
   * */
   public static String getRandomChar(int length) {
      char[] ss = new char[length];
      int i=0;
      while(i<length) {
         int f = (int) (Math.random()*3);
         if(f==0)
            ss[i] = (char) ('A'+Math.random()*26);
         else if(f==1)
            ss[i] = (char) ('a'+Math.random()*26);
         else
            ss[i] = (char) ('0'+Math.random()*10);
         i++;
      }
      String str=new String(ss);
      return str;
   }

   /*
   * 线程随机休眠
   * */
   public static void randomSleep(int second) {
      try {
         Random random = new Random();
         int sleepTime = random.nextInt(second) + 1;
         Thread.sleep(sleepTime * 1000);
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }
   }
}
