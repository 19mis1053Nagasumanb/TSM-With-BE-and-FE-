package com.example.taskmangementsystem.g.services;

import com.example.taskmangementsystem.g.models.DbTask;
import com.example.taskmangementsystem.g.models.EsTask;
import com.example.taskmangementsystem.g.repositorys.EsTaskRepo;
import com.example.taskmangementsystem.g.repositorys.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TaskService {
    @Autowired
    private EsTaskRepo esTaskRepo;

    @Autowired
    private  ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private TaskRepository taskRepository;

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

@Transactional
public String insertTask(EsTask task) {
    System.out.println("saving task");

    // Validation logic
    if (!isNameValid(task.getUsername())) {
        throw new IllegalArgumentException("Invalid name.");
    }
    if (!isLogHoursValid(task.getLogHours())) {
        throw new IllegalArgumentException("Invalid log hours.");
    }
    System.out.println("this is time" + task.getTime());

    // Convert EsTask to DbTask
    DbTask dbTask = convertToDbTask(task);

    // Save to database repository first
    DbTask savedDbTask = taskRepository.save(dbTask);

    // Use the UUID from DbTask as the ID for EsTask
    task.setId(savedDbTask.getId().toString());

    // Save to ElasticSearch repository
    esTaskRepo.save(task);

    // Return the UUID as a string
    return savedDbTask.getId().toString();
}

    private DbTask convertToDbTask(EsTask esTask) {
        DbTask dbTask = new DbTask();
        dbTask.setId(UUID.randomUUID());
        dbTask.setUsername(esTask.getUsername());
        dbTask.setTask(esTask.getTask());
        dbTask.setPriority(esTask.getPriority());
        dbTask.setLogHours(esTask.getLogHours());
        dbTask.setDay(esTask.getDay());
        dbTask.setStatus(esTask.getStatus());
        dbTask.setDate(esTask.getDate());
        dbTask.setTime(esTask.getTime());

        return dbTask;
    }


    public Iterable<EsTask>getTask(){
        return esTaskRepo.findAll();
    }

    public List<EsTask> getTasksWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("the pageable {}",pageable);
        Page<EsTask> paginatedTasks = esTaskRepo.findAll(pageable);
        return paginatedTasks.getContent();  // Returns only the content of the page
    }

    public List<EsTask> getTasksByUsername(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return esTaskRepo.findByUsername(username, pageable).getContent();
    }



    public void deleteAllTasks()
    {
        esTaskRepo.deleteAll();
        taskRepository.deleteAll();
    }

    @Transactional
    public Void  deleteTask(String id){
        esTaskRepo.deleteById(id);
        taskRepository.deleteById(UUID.fromString(id));

        return null;

    }

    public EsTask getTaskById(String id) {
        // Fetch task from Elasticsearch repository
        Optional<EsTask> taskOptional = esTaskRepo.findById(id);

        // Check if task exists in Elasticsearch
        EsTask esTask = taskOptional.orElseThrow(() -> new RuntimeException("Task not found in Elasticsearch with id: " + id));

        // Optionally, fetch the corresponding task from the database
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ID format for database lookup: " + id, e);
        }

        Optional<DbTask> dbTaskOptional = taskRepository.findById(uuid);
        if (dbTaskOptional.isEmpty()) {
            System.out.println("Warning: Task not found in database for id: " + id);
        } else {
            DbTask dbTask = dbTaskOptional.get();
            System.out.println("Task found in database: " + dbTask);
        }

        return esTask;
    }


    public EsTask updateTask(EsTask task, String id) {
        Optional<EsTask>existingTaskOptional=esTaskRepo.findById(id);
        if(existingTaskOptional.isEmpty()){
            throw new RuntimeException("Task not found with id"+id);
        }
        EsTask existingTask = existingTaskOptional.get();
        if (!isNameValid(task.getUsername())) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!isLogHoursValid(task.getLogHours())) {
            throw new IllegalArgumentException("Invalid log hours.");
        }


        existingTask.setUsername(task.getUsername());
        existingTask.setLogHours(task.getLogHours());
        existingTask.setTask(task.getTask());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setDate(task.getDate());
        existingTask.setDay(task.getDay());
        existingTask.setTime(task.getTime());

        EsTask updatedEsTask = esTaskRepo.save(existingTask);

        // Update the corresponding database task
        Optional<DbTask> dbTaskOptional = taskRepository.findById(UUID.fromString(id));
        if (dbTaskOptional.isPresent()) {
            DbTask dbTask = dbTaskOptional.get();
            dbTask.setUsername(task.getUsername());
            dbTask.setLogHours(task.getLogHours());
            dbTask.setTask(task.getTask());
            dbTask.setPriority(task.getPriority());
            dbTask.setStatus(task.getStatus());
            dbTask.setDate(task.getDate());
            dbTask.setDay(task.getDay());
            dbTask.setTime(task.getTime());

            // Save the updated task to the database
            taskRepository.save(dbTask);
        }

        return updatedEsTask;
    }

    public List<EsTask> searchByQuery(String query) {
    List<Criteria> criteriaList = new ArrayList<>();
    criteriaList.add(Criteria.where("username").matches(query));
    criteriaList.add(Criteria.where("task").matches(query));
    criteriaList.add(Criteria.where("priority").matches(query));
    criteriaList.add(Criteria.where("logHours").matches(query));
    criteriaList.add(Criteria.where("day").matches(query));
    criteriaList.add(Criteria.where("status").matches(query));

    Criteria combinedCriteria = new Criteria();
    for (Criteria criteria : criteriaList) {
        combinedCriteria = combinedCriteria.or(criteria);
    }

    CriteriaQuery criteriaQuery = new CriteriaQuery(combinedCriteria);
    SearchHits<EsTask> searchHits = elasticsearchOperations.search(criteriaQuery, EsTask.class);
    return searchHits.stream().map(SearchHit::getContent).toList();

    }



//pagination code


    public List<EsTask> searchTasksWithPagination(String query, int page, int size) {
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
        SearchHits<EsTask> searchHits = elasticsearchOperations.search(criteriaQuery, EsTask.class);

        // Collect and return search results as a list of Tasks
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }


    public List<EsTask> searchByQueryWithPagination(String query, int page, int size) {
        // Initialize the list of criteria
        List<Criteria> criteriaList = new ArrayList<>();

        // Add criteria for each field except date and id
        criteriaList.add(Criteria.where("username").matches(query));
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
        SearchHits<EsTask> searchHits = elasticsearchOperations.search(criteriaQuery, EsTask.class);
        return searchHits.stream().map(SearchHit::getContent).toList();
    }


}
