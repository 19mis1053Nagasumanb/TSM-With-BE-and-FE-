package com.example.taskmangementsystem.g.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.taskmangementsystem.g.models.EsTask;
import com.example.taskmangementsystem.g.utils.ESUtil;
import com.example.taskmangementsystem.g.utils.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;
@Service
@Slf4j
public class ESService {
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    //integrated with pagination
    public SearchResponse<EsTask> fuzzySearch(String approximateTaskName, String fuzziness, int page, int size ) throws IOException {
        System.out.println("hello ");
        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(approximateTaskName, fuzziness);

        SearchResponse<EsTask> response = elasticsearchClient.search(s -> s
                        .index("optimizedes")
                        .query(supplier.get())
                        .from(page * size)  // Skip to the right page
                        .size(size),        // Number of results per page
                EsTask.class
        );

        System.out.println("Elasticsearch supplier fuzzy query: " + supplier.get().toString());
        return response;
    }
    // Fuzzy search by username
    public SearchResponse<EsTask> fuzzySearchByUsername(String username, String fuzziness) throws IOException {
        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(username, fuzziness);
        SearchResponse<EsTask> response = elasticsearchClient.search(s -> s
                .index("optimizedes")
                .query(supplier.get()), EsTask.class);

        System.out.println("Elasticsearch supplier fuzzy query (username): " + supplier.get().toString());
        return response;
    }

    public SearchResponse<EsTask> autoSuggestTask(String searchTerm, String field, int page, int size) throws IOException {
        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(searchTerm, field);

        SearchResponse<EsTask> searchResponse = elasticsearchClient.search(s -> s
                        .index("optimizedes")
                        .query(supplier.get())
                        .from(page * size)   // Calculate the offset
                        .size(size),         // Define the number of results per page
                EsTask.class
        );

        System.out.println("Elasticsearch auto-suggestion query: " + supplier.get().toString());
        log.info("the search response:{}", searchResponse);

        return searchResponse;
    }


}
