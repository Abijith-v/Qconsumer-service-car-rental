package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    public final static String VALIDATE_TOKEN_API_ENDPOINT = "http://localhost:8080/auth/validate";

    public final static String GET_USERNAME_FROM_TOKEN_API_ENDPOINT = "http://localhost:8080/auth/get/username";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public boolean validateToken(String token) {
        try {
            String username = this.getEmailFromToken(token);
            // Call validate token API
            if (userRepository.existsByEmail(username)) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                String requestBody = "{\"username\": \"" + username + "\"}";
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        VALIDATE_TOKEN_API_ENDPOINT,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                    return jsonResponse.get("tokenValid").asBoolean();
                } else {
                    System.out.println("500 code while calling " + VALIDATE_TOKEN_API_ENDPOINT);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public String getEmailFromToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    GET_USERNAME_FROM_TOKEN_API_ENDPOINT,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.out.println("500 code while fetching username from " + GET_USERNAME_FROM_TOKEN_API_ENDPOINT);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
}
