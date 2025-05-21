package com.todo.summary.controller;

import com.todo.summary.model.Todo;
import com.todo.summary.service.SummaryService;
import com.todo.summary.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private SummaryService summaryService;

    @GetMapping("/todos")
    public List<Todo> getTodos() {
        return todoService.getAllTodos();
    }

    @PostMapping("/todos")
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.saveTodo(todo);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodoById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarizeAndSendToSlack() {
        String message = summaryService.summarizeAndSend();
        return ResponseEntity.ok(message);
    }
}