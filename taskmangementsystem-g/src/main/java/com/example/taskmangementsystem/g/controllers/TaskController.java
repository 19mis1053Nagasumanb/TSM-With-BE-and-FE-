package com.example.taskmangementsystem.g.controllers;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.taskmangementsystem.g.models.EsTask;
import com.example.taskmangementsystem.g.services.ESService;
import com.example.taskmangementsystem.g.services.TaskService;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis")
@CrossOrigin(origins = "http://localhost:4200")
@Document(indexName = "optimizedES")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ESService esService;


    @GetMapping("/findAll")
    public List<EsTask> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("the size{}", size );
        log.info("the page {}",page);

        return taskService.getTasksWithPagination(page, size);
    }
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/user-tasks")
    public ResponseEntity<List<EsTask>> getUserTasks(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Logged-in users username: {}", username);

        // Fetch tasks for the user
        List<EsTask> tasks = taskService.getTasksByUsername(username, page, size);
        if (tasks.isEmpty()) {
            log.info("No tasks found for username : {}",username);
        }
        return ResponseEntity.ok(tasks);
    }
    @PostMapping("/insert")
    public ResponseEntity<?> createTask(@RequestBody EsTask task) {
        try {
            log.info("the task {}",task);
            taskService.insertTask(task);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllTasks(){
        log.info("Deleting all tasks from the database");
        taskService.deleteAllTasks();
        return ResponseEntity.ok("All tasks deleted successfully");
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTaskById(@PathVariable String id) {
        System.out.println("Deleting task");
        taskService.deleteTask(id);
        Map<String, String> response = Map.of("message", "Task with ID " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public EsTask getTasksById(@PathVariable String id){
        System.out.println("Getting task by id");
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public EsTask updateTasksById(@RequestBody EsTask task,@PathVariable String id){
        System.out.println("updating task by id"+id);
        return taskService.updateTask(task,id);
    }


    @GetMapping("/fuzzySearch/{approximateTaskName}")
    public List<EsTask> fuzzySearch(
            @PathVariable String approximateTaskName,
            @RequestParam(defaultValue = "AUTO") String fuzziness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) throws IOException {

        SearchResponse<EsTask> searchResponse = esService.fuzzySearch(approximateTaskName, fuzziness, page, size);
        List<Hit<EsTask>> hitList = searchResponse.hits().hits();

        List<EsTask> taskList = new ArrayList<>();
        log.info("the task list {}", taskList);
        for (Hit<EsTask> hit : hitList) {
            taskList.add(hit.source());
        }
        return taskList;
    }

    @GetMapping("/autoSuggest/{field}/{searchTerm}")
    public List<String> autoSuggestTaskSearch(
            @PathVariable String field,
            @PathVariable String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) throws IOException {

        SearchResponse<EsTask> searchResponse = esService.autoSuggestTask(searchTerm, field, page, size);
        List<Hit<EsTask>> hitList = searchResponse.hits().hits();
        log.info("the field:{}", field);
        log.info("the searchTerm:{}", searchTerm);

        List<String> result = new ArrayList<>();
        for (Hit<EsTask> hit : hitList) {
            EsTask task = hit.source();
            if (task != null) {
                switch (field) {
                    case "name":
                        result.add(task.getUsername());
                        break;
                    case "task":
                        result.add(task.getTask());
                        break;
                    case "priority":
                        result.add(task.getPriority().toString());
                        break;
                    case "logHours":
                        result.add(task.getLogHours());
                        break;
                    case "day":
                        result.add(task.getDay());
                        break;
                    case "status":
                        result.add(task.getStatus().toString());
                        break;
                    case "date":
                        result.add(task.getFormattedDate());
                        break;
                    default:
                        result.add("Invalid field");
                }
            }
        }
        log.info("the result:{}", result);
        return result;
    }

    @GetMapping("/search")
    public List<EsTask> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return taskService.searchByQueryWithPagination(query, page, size);
    }

    //pagination
    @GetMapping("/pagination")
    public List<EsTask> searchTasksWithPagination(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("the pagination size {}",size);
        log.info("the pagination page {}", page);


        return taskService.searchTasksWithPagination(query, page, size);
    }

}
