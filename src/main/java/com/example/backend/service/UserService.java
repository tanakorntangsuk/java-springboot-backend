package com.example.backend.service;

import com.example.backend.business.EmailBusiness;
import com.example.backend.dto.*;
import com.example.backend.entity.User;
import com.example.backend.exception.BaseException;
import com.example.backend.exception.UserException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.SecurityUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final EmailBusiness emailBusiness;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, TokenService tokenService, EmailBusiness emailBusiness) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.emailBusiness = emailBusiness;
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

    public LoginResponse login(LoginRequest request) throws BaseException {
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

            // ตรวจสอบว่า ยืนยัน activate รึยัง
            if (!user.isActivated()){
                throw UserException.loginFailUserUnActivated();
            }

            // JWT
            LoginResponse response = new LoginResponse();
            response.setToken(tokenService.tokenize(user));
            return response;
        }
    }

    public RegisterResponse create(RegisterRequest request) throws BaseException {
        {
            String email = request.getEmail();
            String name = request.getName();
            String password = request.getPassword();
            String token = SecurityUtil.generateToken();

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
            u.setToken(token);
            u.setTokenExpire(nextMinute(30));

            User user = userRepository.save(u);

            //ส่งเมลหลักสร้าง user
            sendEmail(user);

            // map -> response
            return userMapper.toRegisterResponse(user);
        }
    }

    private Date nextMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    private void sendEmail(User user) {
        String token = user.getToken();
        try {
            emailBusiness.sendActivateUserEmail(user.getEmail(), user.getName(), token);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    public ActivateResponse activate(ActivateRequest request) throws BaseException {
        String token = request.getToken();
        if (StringUtil.isNullOrEmpty(token)){
            throw UserException.activateNoToken();
        }
        Optional<User> opt = userRepository.findByToken(token);
        if (opt.isEmpty()){
            throw UserException.activateFail();
        }
        User user = opt.get();
        Date now = new Date();
        Date expireDate = user.getTokenExpire();

        // Token หมดอายุ
        if (now.after(expireDate)){
            // TODO: re-email

            throw UserException.activateTokenExpireDate();
        }
        user.setActivated(true);
        userRepository.save(user);

        ActivateResponse response = new ActivateResponse();
        response.setSuccess(true);
        return response;
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
