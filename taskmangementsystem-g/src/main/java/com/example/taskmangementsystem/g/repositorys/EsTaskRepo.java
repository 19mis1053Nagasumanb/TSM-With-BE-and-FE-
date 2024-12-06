package com.example.taskmangementsystem.g.repositorys;

import com.example.taskmangementsystem.g.models.EsTask;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface EsTaskRepo extends ElasticsearchRepository<EsTask, String> {

    Page<EsTask> findByUsername(String username, Pageable pageable);




}
