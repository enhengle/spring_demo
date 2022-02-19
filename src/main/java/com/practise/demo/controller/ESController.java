package com.practise.demo.controller;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author lingwang
 * @date 2022/2/19 19:40
 */
@RestController
@RequestMapping("es")
public class ESController {

    @Resource(description = "esRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Object getList() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("test");

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);
        return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    }


}
