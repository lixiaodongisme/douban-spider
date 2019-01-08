package com.lixiaodongisme.douban_spider.util;

import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    public static void get(String url) {
        CloseableHttpClient client = HttpClients.custom().setDefaultHeaders(null).build();
    }

    private static void setHeaders() {
        List<Header> headerList = new ArrayList<Header>();


    }
}
