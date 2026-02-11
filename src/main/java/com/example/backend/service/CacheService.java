package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.exception.BaseException;
import com.example.backend.exception.UserException;
import com.example.backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Log4j2
public class CacheService {

    private final UserRepository userRepository;

    public CacheService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public Optional<User> findById(String id) {
        log.info("Load User From DB : " + id );
        return userRepository.findById(id);
    }

    // cache แบบอัพเดทตามใน db ด้วยเมื่อมีการอัพเดท
    @CachePut(value = "user", key = "#id")
    public User updateNameById(String id, String name) throws BaseException {
        User user = userRepository.findById(id).orElseThrow(UserException::notFound);

        user.setName(name);
        return userRepository.save(user);
    }

    // cache เมื่อลบ data ออกจาก database
    @CacheEvict(value = "user", key = "#id")
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @CacheEvict(value = "user", allEntries = true)
    public void deleteAll(){}
}