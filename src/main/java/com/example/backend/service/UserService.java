package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.BaseException;
import com.example.backend.exception.UserException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String refreshToken() throws BaseException {
        Optional<String> opt = SecurityUtil.getCurrentUserId();
        if (opt.isEmpty()) {
            throw UserException.unauthorized();
        }

        String userId = opt.get();

        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw UserException.notFound();
        }
        User user = optUser.get();
        return tokenService.tokenize(user);
    }

    public String login(LoginRequest request) throws BaseException {
        {
            String email = request.getEmail();
            String password = request.getPassword();

            // validate
            Optional<User> opt = userRepository.findByEmail(email); // Query database หา user ตาม email
            if (opt.isEmpty()) { // ถ้าไม่เจอ user
                throw UserException.loginFailEmailNotFound();
            }

            User user = opt.get();
            if (!passwordEncoder.matches(password, user.getPassword())) { //เช็ครหัสผ่าน hash password ที่ user กรอก เทียบกับ hash ใน DB
                throw UserException.loginFailPasswordIncorrect();
            }

            // TODO JWT
            return tokenService.tokenize(user);
        }
    }

    public RegisterResponse create(RegisterRequest request) throws BaseException {
        {
            String email = request.getEmail();
            String name = request.getName();
            String password = request.getPassword();

            // validate
            if (Objects.isNull(email)) {
                throw UserException.createEmailNull();
            }
            if (Objects.isNull(password)) {
                throw UserException.createPasswordNull();
            }
            if (Objects.isNull(name)) {
                throw UserException.createNameNull();
            }

            // verify
            if (userRepository.existsByEmail(email)) { //เช็คว่าเมลซ้ำไหม
                throw UserException.createEmailDuplicated();
            }

            // save
            User u = new User();
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setName(name);

            User user = userRepository.save(u);

            // map -> response
            return userMapper.toRegisterResponse(user);
        }
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User updateNameById(String id, String name) throws BaseException {
        User user = userRepository.findById(id).orElseThrow(UserException::notFound);

        user.setName(name);
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
