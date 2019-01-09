package com.lixiaodongisme.douban_spider.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lixiaodongisme.douban_spider.config.RequestHeader;
import com.lixiaodongisme.douban_spider.constant.Constant;
import com.lixiaodongisme.douban_spider.dao.SpiderDao;
import com.lixiaodongisme.douban_spider.entity.Movie;
import com.lixiaodongisme.douban_spider.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpiderService {
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
    public String spiderDouBan() {
        try {
            String target = requestHeader.getTarget();
            Map<String, String> param = new HashMap<>();
            param.put(Constant.SORT, Constant.SORT_BY_RATE);
            param.put(Constant.TAGS, "");
            param.put(Constant.RANGE, "1,2");
            param.put(Constant.START, String.valueOf(0));

            URIBuilder requestUri = HttpUtils.getRequestUri(target, param);
            String data = HttpUtils.get(requestUri, setHeaders());

            if (StringUtils.isNotBlank(data)) {
                List<Movie> movieList = parseJsonToData(data);
                movieList.stream().forEach(movie -> {
                    spiderDao.insert(movie);
                });
            }
            return data;

        }
        catch(URISyntaxException e) {
            e.printStackTrace();
            return "11";
        }
        catch(IOException e) {
            e.printStackTrace();
            return "11";
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
                String directors = obj.getJSONArray("directors").toString();
//                directors = directors.substring(1, directors.length() - 1);
//                directors = directors.substring(directors.length() - 2, directors.length() - 1);
                movie.setDirectors(directors);
            }
            if (obj.getJSONArray("casts") != null) {
                String casts = obj.getJSONArray("casts").toString();
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

    /*
     * 模拟浏览器设置请求头信息
     * */
    private List<Header> setHeaders() {
        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new BasicHeader("host", requestHeader.getHost()));
        headerList.add(new BasicHeader("accept", requestHeader.getAccept()));
        headerList.add(new BasicHeader("accept-language", requestHeader.getAcceptLanguage()));
        headerList.add(new BasicHeader("accept-encoding", requestHeader.getAcceptEncoding()));
        headerList.add(new BasicHeader("referer", requestHeader.getReferer()));
        headerList.add(new BasicHeader("connection", requestHeader.getConnection()));
        headerList.add(new BasicHeader("user-agents", requestHeader.getUserAgent()));
        return headerList;
    }
}
