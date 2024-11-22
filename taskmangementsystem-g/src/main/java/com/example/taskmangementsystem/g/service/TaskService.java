package com.example.taskmangementsystem.g.service;

import co.elastic.clients.util.ObjectBuilder;
import com.example.taskmangementsystem.g.entity.Task;
import com.example.taskmangementsystem.g.repository.TaskRepo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.*;

import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchQuery;


@Service
@Slf4j
public class TaskService {
    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private  ElasticsearchOperations elasticsearchOperations;

    public boolean isNameValid(String name) {
        if (name != null) {
            String regex = "^[A-Za-z ]{1,255}$";
            return Pattern.matches(regex, name);
        }
        return false;
    }


    public boolean isLogHoursValid(String logHours) {
        if (logHours != null) {
            String[] parts = logHours.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours < 8 && minutes >= 0 && minutes < 60;
            }
        }
        return false;
    }

    public Task insertTask(Task task){
        System.out.println("saving task");
        if (!isNameValid(task.getName())) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!isLogHoursValid(task.getLogHours())) {
            throw new IllegalArgumentException("Invalid log hours.");
        }
        System.out.println("this is time"+task.getTime());
        return taskRepo.save(task);
    }

    public Iterable<Task>getTask(){
        return taskRepo.findAll();
    }

    public List<Task> getTasksWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("the pageable {}",pageable);
        Page<Task> paginatedTasks = taskRepo.findAll(pageable);

        return paginatedTasks.getContent();  // Returns only the content of the page
    }

    public void deleteAllTasks(){
        taskRepo.deleteAll();
    }

    public Void  deleteTask(String id){
        taskRepo.deleteById(id);
        return null;
    }

    public Task getTaskById(String id){
        Optional<Task>taskOptional =taskRepo.findById(id);
        return taskOptional.orElseThrow(()->new RuntimeException("Task not found with id:"+id));
    }

    public Task updateTask(Task task, String id) {
        Optional<Task>existingTaskOptional=taskRepo.findById(id);
        if(existingTaskOptional.isEmpty()){
            throw new RuntimeException("Task not found with id"+id);
        }
        Task existingTask = existingTaskOptional.get();
        if (!isNameValid(task.getName())) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!isLogHoursValid(task.getLogHours())) {
            throw new IllegalArgumentException("Invalid log hours.");
        }


        existingTask.setName(task.getName());
        existingTask.setLogHours(task.getLogHours());
        existingTask.setTask(task.getTask());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setDate(task.getDate());
        existingTask.setDay(task.getDay());
        existingTask.setTime(task.getTime());

        return taskRepo.save(existingTask);
    }


//    public List<Task> searchByQuery(String query) {
//        Criteria criteria = Criteria.where("name").matches(query);
//        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
//
//        return elasticsearchOperations.search(criteriaQuery, Task.class)
//                .map(SearchHit::getContent)
//                .toList();
//
//
//    }

    public List<Task> searchByQuery(String query) {
        // Initialize the list of criteria
        List<Criteria> criteriaList = new ArrayList<>();

        // Add criteria for each field except date and id
        criteriaList.add(Criteria.where("name").matches(query));
        criteriaList.add(Criteria.where("task").matches(query));
        criteriaList.add(Criteria.where("priority").matches(query));
        criteriaList.add(Criteria.where("logHours").matches(query));
        criteriaList.add(Criteria.where("day").matches(query));
        criteriaList.add(Criteria.where("status").matches(query));



        // Combine criteria with OR operation
        Criteria combinedCriteria = new Criteria();
        for (Criteria criteria : criteriaList) {
            combinedCriteria = combinedCriteria.or(criteria);
        }
        log.info("the combinedCriteria {} ", combinedCriteria);
        // Create CriteriaQuery with combined criteria
        CriteriaQuery criteriaQuery = new CriteriaQuery(combinedCriteria);

        // Execute search and map results
        SearchHits<Task> searchHits = elasticsearchOperations.search(criteriaQuery, Task.class);
        return searchHits.stream().map(SearchHit::getContent).toList();
    }


//pagination code


    public List<Task> searchTasksWithPagination(String query, int page, int size) {
        // Start with a base criteria that matches the "query" against multiple fields except "id" and "date"
        Criteria criteria = new Criteria()
                .or("name").matches(query)
                .or("task").matches(query)
                .or("priority").matches(query)
                .or("logHours").matches(query)
                .or("day").matches(query)
                .or("status").matches(query);

        // Apply pagination
        Pageable pageable = PageRequest.of(page, size);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria).setPageable(pageable);

        // Perform the search
        SearchHits<Task> searchHits = elasticsearchOperations.search(criteriaQuery, Task.class);

        // Collect and return search results as a list of Tasks
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }


    public List<Task> searchByQueryWithPagination(String query, int page, int size) {
        // Initialize the list of criteria
        List<Criteria> criteriaList = new ArrayList<>();

        // Add criteria for each field except date and id
        criteriaList.add(Criteria.where("name").matches(query));
        criteriaList.add(Criteria.where("task").matches(query));
        criteriaList.add(Criteria.where("priority").matches(query));
        criteriaList.add(Criteria.where("logHours").matches(query));
        criteriaList.add(Criteria.where("day").matches(query));
        criteriaList.add(Criteria.where("status").matches(query));

        // Combine criteria with OR operation
        Criteria combinedCriteria = new Criteria();
        for (Criteria criteria : criteriaList) {
            combinedCriteria = combinedCriteria.or(criteria);
        }

        // Create CriteriaQuery with combined criteria and apply pagination
        Pageable pageable = PageRequest.of(page, size);
        CriteriaQuery criteriaQuery = new CriteriaQuery(combinedCriteria).setPageable(pageable);

        // Execute search and map results
        SearchHits<Task> searchHits = elasticsearchOperations.search(criteriaQuery, Task.class);
        return searchHits.stream().map(SearchHit::getContent).toList();
    }


}
