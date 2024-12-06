package com.example.taskmangementsystem.g.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import lombok.val;

import java.util.function.Supplier;

public class ESUtil {
    public static Supplier<Query> createSupplierAutoSuggest(String searchTerm, String field) {
        Supplier<Query> supplier = () -> Query.of(q -> q.match(createAutoSuggestMatchQuery(searchTerm, field)));
        return supplier;
    }

    public static MatchQuery createAutoSuggestMatchQuery(String searchTerm, String field) {
        val autoSuggestQuery = new MatchQuery.Builder();
        return autoSuggestQuery.field(field).query(searchTerm).analyzer("standard")
                .fuzziness("3")
                .build();


    }
}
