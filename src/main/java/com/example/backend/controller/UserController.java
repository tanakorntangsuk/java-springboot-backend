package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.exception.BaseException;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.RegisterService;
import com.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UserController {

    private final RegisterService registerService;
    private final UserService userService;

    public UserController(RegisterService registerService, UserService userService) {
        this.registerService = registerService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) throws BaseException {
        String response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) throws BaseException {
        RegisterResponse user = userService.create(request);
        return ResponseEntity.ok(user);
    }

    // การส่งรูป/ไฟล์
    @PostMapping
    public ResponseEntity<String> uploadProfilePicture(@RequestPart MultipartFile file) throws BaseException {
        String response = registerService.uploadProfilePicture(file);
        return ResponseEntity.ok(response);
    }
}
