package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.Social;
import com.example.backend.entity.User;
import com.example.backend.exception.BaseException;
import com.example.backend.exception.UserException;
import com.example.backend.repository.SocialRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class SocialService {

    private final SocialRepository socialRepository;

    public SocialService(SocialRepository socialRepository) {
        this.socialRepository = socialRepository;
    }

    public Optional<Social> findByUser(User user){
        return socialRepository.findByUser(user);
    }

    public Social create(User user, String facebook, String line, String instagram, String tiktok){

        Social social = new Social();

        social.setUser(user);
        social.setFacebook(facebook);
        social.setLine(line);
        social.setInstagram(instagram);
        social.setTiktok(tiktok);

        return socialRepository.save(social);
    }

}