package com.lixiaodongisme.douban_spider.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lixiaodongisme.douban_spider.config.RequestHeader;
import com.lixiaodongisme.douban_spider.constant.Constant;
import com.lixiaodongisme.douban_spider.dao.SpiderDao;
import com.lixiaodongisme.douban_spider.entity.Movie;
import com.lixiaodongisme.douban_spider.util.HttpUtils;
import com.lixiaodongisme.douban_spider.util.RandomUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class SpiderService {
    private static Logger log = LoggerFactory.getLogger(SpiderService.class);

    // 从1分电影开始获取
    private static final int INIT_RATE = 1;

    private static final int INIT_START = 0;

    private static final int PAGE_SIZE = 20;

    private static final int BID_LENGTH = 11;

    private static List<Movie> batchList = new ArrayList<>();

    @Autowired
    private RequestHeader requestHeader;

    @Autowired
    private SpiderDao spiderDao;

    /*
    * 获取数据的地址为https://movie.douban.com/j/new_search_subjects?sort=R&range=0,10&tags=&start=0
    *   sort为排序规则
    *   tags为标签(电影、电视剧、短片...)
    *   range为分值(0-1分为无评分电影，大部分为未上映电影)
    *   start从0开始，每次递增20
    * */
    public void spiderMovie() {
        log.info("My spider is running!!!");

        try {
            String target = requestHeader.getTarget();
            Map<String, String> param = new HashMap<>();
            param.put(Constant.SORT, Constant.SORT_BY_RATE);
            param.put(Constant.TAGS, "");

            long startTime = System.currentTimeMillis();

            for (int minRate = INIT_RATE, maxRate = minRate + 1; minRate <= 9; minRate++, maxRate++) {

                param.put(Constant.RANGE, minRate + "," + maxRate);

                log.info("target is " + target + ", sort by " + Constant.SORT +
                        ", tags is " + Constant.TAGS + ", range is " + minRate +
                        " to " + maxRate + "...");

                int start = INIT_START;
                boolean spiderFinish = false;

                while (!spiderFinish) {
                    boolean isLastBatch = false;
                    param.put(Constant.START, String.valueOf(start));
                    start += PAGE_SIZE;

                    URIBuilder requestUri = HttpUtils.getRequestUri(target, param);

                    String data = HttpUtils.get(requestUri, setHeaders());

                    if (StringUtils.isNotBlank(data)) {
                        List<Movie> movieList = parseJsonToData(data);
                        if (movieList.size() == 0 || movieList == null) {
                            spiderFinish = true;
                            isLastBatch = true;
                        }
                        batchInsert(movieList, isLastBatch);
                    } else {
                        spiderFinish = true;
                    }
                }
            }

            long endTime = System.currentTimeMillis();
            log.info("spider cost time is " + ((endTime - startTime) / 1000) + "s");
        }
        catch(URISyntaxException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void batchInsert(List<Movie> movieList, boolean isLastBatch) {
        // 对数据去重
        Set<Movie> tempSet = new HashSet<>();
        tempSet.addAll(movieList);
        batchList.addAll(tempSet);

        // 分批导入，每1000条数据导入一次
        if (batchList.size() > 1000) {
            spiderDao.insert(batchList);
            batchList.clear();
        }

        // 导入最后一批数据
        if (isLastBatch) {
            spiderDao.insert(batchList);
            batchList.clear();
        }
    }

    private List<Movie> parseJsonToData(String data) {
        List<Movie> movieList = new ArrayList<>();

        JSONObject jsonObj = JSONObject.parseObject(data);
        JSONArray jsonArray = jsonObj.getJSONArray("data");
        
        for (int i = 0; i < jsonArray.size(); i++) {
            Movie movie = new Movie();
            JSONObject obj = jsonArray.getJSONObject(i);

            String id = obj.getString("id");
            String title = obj.getString("title");
            String cover = obj.getString("cover");
            String url = obj.getString("url");
            Double rate = obj.getDouble("rate");
            if (obj.getJSONArray("directors") != null
                    && obj.getJSONArray("directors").size() > 0) {
                String directors = jsonArrayToString("directors", obj);
                movie.setDirectors(directors);
            }
            if (obj.getJSONArray("casts") != null) {
                String casts = jsonArrayToString("casts", obj);
                movie.setCasts(casts);
            }

            movie.setId(id);
            movie.setTitle(title);
            movie.setCover(cover);
            movie.setUrl(url);
            movie.setRate(rate);

            movieList.add(movie);
        }

        return movieList;
    }

    private String jsonArrayToString(String key, JSONObject obj) {
        String res = ArrayUtils.toString(obj.getJSONArray(key));
        res = res.substring(1, res.length() - 1);
        res = res.replace("\"", "");
        return res;
    }

    /*
     * 模拟浏览器设置请求头信息
     * */
    private List<Header> setHeaders() {
        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new BasicHeader("Host", requestHeader.getHost()));
        headerList.add(new BasicHeader("Accept", requestHeader.getAccept()));
        headerList.add(new BasicHeader("Accept-Language", requestHeader.getAcceptLanguage()));
        headerList.add(new BasicHeader("Accept-Encoding", requestHeader.getAcceptEncoding()));
        headerList.add(new BasicHeader("Referer", requestHeader.getReferer()));
        headerList.add(new BasicHeader("Connection", requestHeader.getConnection()));
        headerList.add(new BasicHeader("User-Agents", requestHeader.getUserAgent()));
//        headerList.add(new BasicHeader("Cookie", "bid=" + RandomUtil.getRandomChar(BID_LENGTH)));
//        headerList.add(new BasicHeader("Cookie", "bid=qX-qYHj6L_A"));

        return headerList;
    }
}
