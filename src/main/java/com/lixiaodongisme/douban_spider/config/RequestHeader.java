package com.lixiaodongisme.douban_spider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Configuration
@ConfigurationProperties(prefix="config", ignoreUnknownFields = false)
@PropertySource("classpath:properties/config.properties")
public class RequestHeader {
    // 目标网址
    @Value("${target}")
    private String target;

    @Value("${user-agents}")
    private String userAgent;

    @Value("${host}")
    private String host;

    @Value("${accept}")
    private String accept;

    @Value("${accept-language}")
    private String acceptLanguage;

    @Value("${accept-encoding}")
    private String acceptEncoding;

    @Value("${referer}")
    private String referer;

    @Value("${connection}")
    private String connection;

    public String getUserAgent() {
        String[] userAgentArray = this.userAgent.split("@");
        Random random = new Random();
        int index = random.nextInt(userAgentArray.length);
        return userAgentArray[index];
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }
}
