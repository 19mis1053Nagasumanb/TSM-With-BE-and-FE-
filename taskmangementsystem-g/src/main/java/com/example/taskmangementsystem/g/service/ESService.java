package com.example.taskmangementsystem.g.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.taskmangementsystem.g.entity.Task;
import com.example.taskmangementsystem.g.util.ESUtil;
import com.example.taskmangementsystem.g.util.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;
@Service
public class ESService {
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public SearchResponse<Task>fuzzySearch(String approximateTaskName,String fuzziness) throws IOException {
        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(approximateTaskName,fuzziness);
        SearchResponse<Task>response = elasticsearchClient
                .search(s->s.index("optimizedes").query(supplier.get()),Task.class);
        System.out.println("elasticsearch supplier fuzzy query "+supplier.get().toString());
        return response;
    }


    //integrated with pagination
    public SearchResponse<Task> fuzzySearch(String approximateTaskName, String fuzziness, int page, int size) throws IOException {
        System.out.println("hello ");
        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(approximateTaskName, fuzziness);

        SearchResponse<Task> response = elasticsearchClient.search(s -> s
                        .index("optimizedes")
                        .query(supplier.get())
                        .from(page * size)  // Skip to the right page
                        .size(size),        // Number of results per page
                Task.class
        );

        System.out.println("Elasticsearch supplier fuzzy query: " + supplier.get().toString());
        return response;
    }


    //for suggestions


//    public SearchResponse<Task> autoSuggestTask(String searchTerm, String field) throws IOException {
//        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(searchTerm, field);
//        SearchResponse<Task> searchResponse = elasticsearchClient
//                .search(s -> s.index("optimizedes").query(supplier.get()), Task.class);
//        System.out.println("Elasticsearch auto-suggestion query: " + supplier.get().toString());
//        return searchResponse;
//    }

    public SearchResponse<Task> autoSuggestTask(String searchTerm, String field, int page, int size) throws IOException {
        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(searchTerm, field);

        SearchResponse<Task> searchResponse = elasticsearchClient.search(s -> s
                        .index("optimizedes")
                        .query(supplier.get())
                        .from(page * size)   // Calculate the offset
                        .size(size),         // Define the number of results per page
                Task.class
        );

        System.out.println("Elasticsearch auto-suggestion query: " + supplier.get().toString());
        return searchResponse;
    }
}
