package com.lixiaodongisme.douban_spider.util;

import com.lixiaodongisme.douban_spider.constant.Constant;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static final int SLEEP_TIME = 5;

    private static CloseableHttpResponse response;

    private static RequestConfig config;

    private static CloseableHttpClient client;

    private static HttpHost proxy;

    private static Map<String, Object> porxyIp;

    private static HttpClientContext httpContext;

    private static final String PROXY_IP_API = "http://api.xdaili" +
            ".cn/xdaili-api//privateProxy/getDynamicIP/DD201911135305H65zj" +
            "/a2acdb11832111e7bcaf7cd30abda612?returnType=2";

    private HttpUtils() {}

    static {
        httpContext = HttpClientContext.create();

        porxyIp = ProxyIpPool.getProxyIpPool().getPorxyIp(PROXY_IP_API);

        if ("拨号失败".equals(porxyIp.get(Constant.ERROR_CODE))) {
            porxyIp = ProxyIpPool.getProxyIpPool().getPorxyIp(PROXY_IP_API);
        }

        proxy = new HttpHost(String.valueOf(porxyIp.get(Constant.HOST)),
                Integer.valueOf(String.valueOf(porxyIp.get(Constant.PORT)
                )));

        log.info("use proxy ip is is " + proxy.getHostName() + ":" + proxy.getPort());
    }

    /*
    * 带参数的get请求
    * */
    public static String get(URIBuilder uriBuilder, List<Header> headers) throws IOException, URISyntaxException {
        String result = null;
        try {
            config = RequestConfig.custom()
                    .setConnectTimeout(10000).setSocketTimeout(10000).setCookieSpec
                            (CookieSpecs.DEFAULT).setProxy(proxy).build();
//            if (proxy != null) {
//                config = RequestConfig.custom()
//                    .setConnectTimeout(10000).setSocketTimeout(10000).setCookieSpec
//                            (CookieSpecs.DEFAULT).setProxy(proxy).build();
//            } else {
//                config = RequestConfig.custom()
//                        .setConnectTimeout(10000).setSocketTimeout(10000).setCookieSpec
//                                (CookieSpecs.DEFAULT).build();
//            }

            client = HttpClients.custom()
                    .setDefaultHeaders(headers).build();

            HttpGet get = new HttpGet(uriBuilder.build());
            get.setConfig(config);

            // 随机休眠1-5秒，以免被检测到
//            RandomUtil.randomSleep(SLEEP_TIME);
            response = client.execute(get, httpContext);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == Constant.OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            } else {
                log.error("Get request is failed, status code is " + statusCode);
                return changeProxyIpAndCleanCookie(uriBuilder, headers);
            }
        } catch (SocketTimeoutException e) {
            log.error("exception is SocketTimeoutException");
            return changeProxyIpAndCleanCookie(uriBuilder, headers);
        } catch (ConnectTimeoutException e) {
            log.error("exception is ConnectTimeoutException");
            return changeProxyIpAndCleanCookie(uriBuilder, headers);
        } finally {
            response.close();
            client.close();
        }
        return result;
    }

    private static String changeProxyIpAndCleanCookie(URIBuilder uriBuilder, List<Header>
            headers) throws IOException, URISyntaxException {
//        httpContext.getCookieStore().clear();
        // 长时间请求后会抛出该异常，切换IP后继续请求
        log.info("Change proxy IP");
        porxyIp = ProxyIpPool.getProxyIpPool().getPorxyIp(PROXY_IP_API);

        if ("拨号失败".equals(porxyIp.get(Constant.ERROR_CODE))) {
            porxyIp = ProxyIpPool.getProxyIpPool().getPorxyIp(PROXY_IP_API);
        }

        proxy = new HttpHost(String.valueOf(porxyIp.get(Constant.HOST)),
                Integer.valueOf(String.valueOf(porxyIp.get(Constant.PORT)
                )));

        log.info("change proxy ip to " + proxy.getHostName() + ":" + proxy.getPort());
        return get(uriBuilder, headers);
    }

    /*
    * 不带参数的get请求
    * */
    public static String get(String url) {
        String result = null;
        try {
            CloseableHttpClient client = HttpClients.custom().build();
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = client.execute(get);

            if (response.getStatusLine().getStatusCode() == Constant.OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
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

//    //使用序列化的方式保存CookieStore到本地文件，方便后续的读取使用
//    private static void saveCookieStore( CookieStore cookieStore, String savePath ) throws IOException {
//        FileOutputStream fs = new FileOutputStream(savePath);
//        ObjectOutputStream os = new ObjectOutputStream(fs);
//        os.writeObject(cookieStore);
//        os.close();
//    }
//
//    private static CookieStore readCookieStore( String savePath ) throws IOException, ClassNotFoundException {
//        FileInputStream fs = new FileInputStream(savePath);
//        ObjectInputStream ois = new ObjectInputStream(fs);
//        CookieStore cookieStore = (CookieStore) ois.readObject();
//        ois.close();
//        return cookieStore;
//    }
}
