package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.User;
import com.example.backend.exception.BaseException;
import com.example.backend.service.RegisterService;
import com.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final RegisterService registerService;
    private final UserService userService;

    public UserController(RegisterService registerService, UserService userService) {
        this.registerService = registerService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value="/profile")
    public ResponseEntity<UserResponse> getMyUsersProfile() throws BaseException {
        UserResponse users = userService.getMyUsersProfile();
        return ResponseEntity.ok(users);
    }

    @PutMapping(value="/profile")
    public ResponseEntity<UserResponse> updateMyUsersProfile(@RequestBody UserRequest request) throws BaseException {
        UserResponse users = userService.updateMyUsersProfile(request);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws BaseException {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) throws BaseException {
        RegisterResponse user = userService.create(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/activate")
    public ResponseEntity<ActivateResponse> activate(@RequestBody ActivateRequest request) throws BaseException {
        ActivateResponse response = userService.activate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-activation-email")
    public ResponseEntity<String> resendActivationEmail(@RequestBody ResendActivationEmailRequest request) throws BaseException {
        String resendEmail = userService.resendActivationEmail(request);
        return ResponseEntity.ok(resendEmail);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshToken() throws BaseException {
        String token = userService.refreshToken();
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/test-delete")
    public ResponseEntity<Void> testDeleteMyAccount() throws BaseException {
        userService.testDeleteMyAccount();
        return ResponseEntity.ok().build();
    }


    // การส่งรูป/ไฟล์
    @PostMapping
    public ResponseEntity<String> uploadProfilePicture(@RequestPart MultipartFile file) throws BaseException {
        String response = registerService.uploadProfilePicture(file);
        return ResponseEntity.ok(response);
    }
}
