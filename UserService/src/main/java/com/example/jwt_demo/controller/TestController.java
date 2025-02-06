package com.example.jwt_demo.controller;

import com.example.jwt_demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/user")
    public ResponseEntity<?> allAccess(@RequestBody String requestBody) {
        // Extract token from request body
        String token = requestBody;
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is missing");
        }

        // Extract user information from the token
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.extractRole(token);

        // Return user information in the response
        return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role,
                "message", "Token is valid"
        ));
    }
    @GetMapping("/all")
    public String userAccess1() {
        return "All Content.";
    }
    @GetMapping("/user1")
    public String userAccess() {
        return "User Content.";
    }
}