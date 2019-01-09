package com.lixiaodongisme.douban_spider.util;

import com.lixiaodongisme.douban_spider.constant.Constant;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUtils {

    private HttpUtils() {}

    public static String get(URIBuilder uriBuilder, List<Header> headers) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultHeaders(headers).build();
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000).setSocketTimeout(5000).setCookieSpec
                        (CookieSpecs.IGNORE_COOKIES).build();

        String result = null;
        HttpGet get = new HttpGet(uriBuilder.build());
        get.setConfig(config);
        CloseableHttpResponse response = client.execute(get);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == Constant.OK) {
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } else {
            result = "请求失败，错误码为：" + statusCode;
        }

        response.close();
        client.close();
        return result;
    }

    /*
    * get请求封装参数
    * */
    public static URIBuilder getRequestUri(String uri, Map<String, String> requestParam) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(uri);
        List<NameValuePair> list = new LinkedList<>();

        Set<Map.Entry<String, String>> entrySet = requestParam.entrySet();
        entrySet.stream().forEach(entry -> {
            BasicNameValuePair param = new BasicNameValuePair
                    (entry.getKey(), entry.getValue());
            list.add(param);
        });

        builder.setParameters(list);
        return builder;
    }
}
