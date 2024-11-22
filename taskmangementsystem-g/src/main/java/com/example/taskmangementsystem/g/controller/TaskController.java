package com.example.taskmangementsystem.g.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.taskmangementsystem.g.entity.Task;
import com.example.taskmangementsystem.g.service.ESService;
import com.example.taskmangementsystem.g.service.TaskService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ESService esService;

//    @GetMapping("/findAll")
//    Iterable<Task>getAllTasks(){
//        return taskService.getTask();
//    }
    @GetMapping("/findAll")
    public List<Task> getAllTasks(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("the size{}", size );
        log.info("the page {}",page);

        return taskService.getTasksWithPagination(page, size);
    }




    @PostMapping("/insert")
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            log.info("the task {}",task);
            taskService.insertTask(task);
//            return ResponseEntity.ok("Task created successfully");
//            return ResponseEntity.ok(Map.of("message", "Task created successfully")); // Returning a JSON object
      return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllTasks(){
        log.info("deleting all data {}",deleteAllTasks());
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
    public Task getTasksById(@PathVariable String id){
        System.out.println("Getting task by id");
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public Task updateTasksById(@RequestBody Task task,@PathVariable String id){
        System.out.println("updating task by id"+id);
        return taskService.updateTask(task,id);
    }


    @GetMapping("/fuzzySearch/{approximateTaskName}")
    public List<Task> fuzzySearch(
            @PathVariable String approximateTaskName,
            @RequestParam(defaultValue = "AUTO") String fuzziness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) throws IOException {

        SearchResponse<Task> searchResponse = esService.fuzzySearch(approximateTaskName, fuzziness, page, size);
        List<Hit<Task>> hitList = searchResponse.hits().hits();

        List<Task> taskList = new ArrayList<>();
        log.info("the task list {}", taskList);
        for (Hit<Task> hit : hitList) {
            taskList.add(hit.source());
        }
        return taskList;
    }

//    @GetMapping("/searchWithSorting")
//    public List<Task> searchWithSorting(
//            @RequestParam String searchTerm,
//            @RequestParam String sortField,
//            @RequestParam String sortOrder,
//            @RequestParam(defaultValue = "AUTO") String fuzziness) throws IOException {
//
//        SearchResponse<Task> searchResponse = esService.searchWithSorting(searchTerm, sortField, sortOrder, fuzziness);
//        List<Hit<Task>> hitList = searchResponse.hits().hits();
//        List<Task> taskList = new ArrayList<>();
//        for (Hit<Task> hit : hitList) {
//            taskList.add(hit.source());
//        }
//        return taskList;
//    }

//    @GetMapping("/searchWithSorting")
//    public ResponseEntity<?> searchWithSorting(
//            @RequestParam String searchTerm,
//            @RequestParam String sortField,
//            @RequestParam String sortOrder,
//            @RequestParam String fuzziness) {
//        try {
//            return ResponseEntity.ok(esService.searchWithSorting(searchTerm, sortField, sortOrder, fuzziness));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }

    @GetMapping("/autoSuggest/{field}/{searchTerm}")
    public List<String> autoSuggestTaskSearch(
            @PathVariable String field,
            @PathVariable String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) throws IOException {

        SearchResponse<Task> searchResponse = esService.autoSuggestTask(searchTerm, field, page, size);
        List<Hit<Task>> hitList = searchResponse.hits().hits();

        List<String> result = new ArrayList<>();
        for (Hit<Task> hit : hitList) {
            Task task = hit.source();
            if (task != null) {
                switch (field) {
                    case "name":
                        result.add(task.getName());
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
        return result;
    }



//    @GetMapping("/search")
//    public List<Task>search(@RequestParam String query){
//
//        return taskService.searchByQuery(query);
//    }

    @GetMapping("/search")
    public List<Task> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return taskService.searchByQueryWithPagination(query, page, size);
    }

    //pagination
    @GetMapping("/pagination")
    public List<Task> searchTasksWithPagination(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("the pagination size {}",size);
        log.info("the pagination page {}", page);


        return taskService.searchTasksWithPagination(query, page, size);
    }

}
