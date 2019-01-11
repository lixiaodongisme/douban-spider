package com.lixiaodongisme.douban_spider.util;

import com.alibaba.fastjson.JSONObject;
import com.lixiaodongisme.douban_spider.constant.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/*
* 调用讯代理API获取代理ip，构造自己的代理ip池
* */
public class ProxyIpPool {
   private static ProxyIpPool proxyIpPool;

   private ProxyIpPool() {
   }

   public static ProxyIpPool getProxyIpPool() {
      if (proxyIpPool == null) {
         synchronized(ProxyIpPool.class) {
            if (proxyIpPool == null) {
               proxyIpPool = new ProxyIpPool();
            }
         }
      }
      return proxyIpPool;
   }

   public static Map<String, Object> getPorxyIp(String url) {
      Map<String, Object> resultMap = new HashMap<>();

      try {
         String data = HttpUtils.get(url);

         if (StringUtils.isNotBlank(data) && !data.contains("拨号失败")) {
            resultMap = parseJsonToData(data);

            int errorCode = Integer.valueOf(String.valueOf(resultMap.get("errorCode")));
            if (errorCode == 10036 || errorCode == 10038 || errorCode == 10055) {
               // 提取ip速度过快的话，等待15秒再去提取
               Thread.sleep(15000);
               data = HttpUtils.get(url);
               resultMap = parseJsonToData(data);
            }
         } else {
            resultMap.put(Constant.ERROR_CODE, "拨号失败");
         }
      } catch(InterruptedException e) {
         e.printStackTrace();
      }

      return resultMap;
   }

   private static Map<String, Object> parseJsonToData(String data) {
      Map<String, Object> ipMap = new HashMap<>();
      if (StringUtils.isNotBlank(data)) {
         JSONObject ipObj = JSONObject.parseObject(data);

         String errorCode = ipObj.getString("ERRORCODE");
         String ip = ipObj.getJSONObject("RESULT").getString("wanIp");
         String port = ipObj.getJSONObject("RESULT").getString("proxyport");

         ipMap.put(Constant.ERROR_CODE, errorCode);
         ipMap.put(Constant.HOST, ip);
         ipMap.put(Constant.PORT, port);
      }
      return ipMap;
   }
}
