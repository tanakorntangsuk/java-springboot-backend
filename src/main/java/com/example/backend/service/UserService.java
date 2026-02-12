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
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final EmailBusiness emailBusiness;

    private final CacheService userCacheService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, TokenService tokenService, EmailBusiness emailBusiness, CacheService userCacheService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.emailBusiness = emailBusiness;
        this.userCacheService = userCacheService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponse getMyUsersProfile() throws BaseException {
        Optional<String> opt = SecurityUtil.getCurrentUserId();
        if (opt.isEmpty()) {
            throw UserException.unauthorized();
        }

        String userId = opt.get();

        Optional<User> optUser = userCacheService.findById(userId);
        if (optUser.isEmpty()) {
            throw UserException.notFound();
        }
        return userMapper.toUserResponse(optUser.get());
    }

    public UserResponse updateMyUsersProfile(UserRequest request) throws BaseException {

        Optional<String> opt = SecurityUtil.getCurrentUserId();
        if (opt.isEmpty()) {
            throw UserException.unauthorized();
        }

        String userId = opt.get();

        // validate name not null
        if (ObjectUtils.isEmpty(request.getName())) {
            throw UserException.updateNameNull();
        }

        User user = userCacheService.updateNameById(userId, request.getName());

        return userMapper.toUserResponse(user);
    }

    public String refreshToken() throws BaseException {
        Optional<String> opt = SecurityUtil.getCurrentUserId();
        if (opt.isEmpty()) {
            throw UserException.unauthorized();
        }

        String userId = opt.get();

        Optional<User> optUser = userCacheService.findById(userId);
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
            if (!user.isActivated()) {
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

    //การยืนยันเมล
    public ActivateResponse activate(ActivateRequest request) throws BaseException {
        String token = request.getToken();
        if (StringUtil.isNullOrEmpty(token)) {
            throw UserException.activateNoToken();
        }
        Optional<User> opt = userRepository.findByToken(token);
        if (opt.isEmpty()) {
            throw UserException.activateFail();
        }

        User user = opt.get();

        if (user.isActivated()) {
            throw UserException.activateAlready();
        }

        Date now = new Date();
        Date expireDate = user.getTokenExpire();

        // Token หมดอายุ
        if (now.after(expireDate)) {
            // TODO: re-email
            throw UserException.activateTokenExpireDate();
        }
        user.setActivated(true);
        userRepository.save(user);

        ActivateResponse response = new ActivateResponse();
        response.setSuccess(true);
        return response;
    }

    public String resendActivationEmail(ResendActivationEmailRequest request) throws BaseException {
        String token = request.getToken();
        if (StringUtil.isNullOrEmpty(token)) {
            throw UserException.resendActivationEmailNoToken();
        }
        Optional<User> opt = userRepository.findByToken(token);
        if (opt.isEmpty()) {
            throw UserException.resendActivationTokenNotFound();
        }

        User user = opt.get();

        if (user.isActivated()) {
            throw UserException.activateAlready();
        }

        // ถ้า token ถูก ส่งเมลใหม่
        user.setToken(SecurityUtil.generateToken());
        user.setTokenExpire(nextMinute(30));
        User save = userRepository.save(user);

        sendEmail(save);
        return "resendActivationEmail";
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void testDeleteMyAccount() throws BaseException {
        Optional<String> opt = SecurityUtil.getCurrentUserId();
        if (opt.isEmpty()) {
            throw UserException.unauthorized();
        }

        String userId = opt.get();
        userCacheService.deleteById(userId);

    }

}
