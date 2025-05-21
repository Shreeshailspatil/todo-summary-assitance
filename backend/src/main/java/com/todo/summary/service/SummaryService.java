package com.todo.summary.service;

import com.todo.summary.model.Todo;
import com.todo.summary.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Autowired
    private TodoRepository todoRepository;

    public String summarizeAndSend() {
        List<Todo> todos = todoRepository.findAll();
        String pendingTasks = todos.stream()
                .filter(todo -> !todo.isCompleted())
                .map(Todo::getTask)
                .collect(Collectors.joining("\n"));

        String summary = callOpenAI(pendingTasks);
        sendToSlack(summary);
        return "Summary sent to Slack.";
    }

    private String callOpenAI(String content) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "https://api.openai.com/v1/chat/completions";

        String requestBody = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\"Summarize the following tasks:\\n" + content + "\"}]}";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization", "Bearer " + openaiApiKey);
        headers.add("Content-Type", "application/json");

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
        var response = restTemplate.postForEntity(endpoint, entity, String.class);
        return response.getBody();
    }

    private void sendToSlack(String message) {
        RestTemplate restTemplate = new RestTemplate();
        String payload = "{\"text\": \"" + message + "\"}";
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(payload);
        restTemplate.postForEntity(slackWebhookUrl, entity, String.class);
    }
}