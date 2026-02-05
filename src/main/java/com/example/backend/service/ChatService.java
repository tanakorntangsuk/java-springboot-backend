package com.example.backend.service;

import com.example.backend.dto.ChatMessageRequest;
import com.example.backend.dto.ChatMessageResponse;
import com.example.backend.exception.BaseException;
import com.example.backend.exception.ChatException;
import com.example.backend.util.SecurityUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatService {

    private final SimpMessagingTemplate template;

    public ChatService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void post(ChatMessageRequest request) throws BaseException {
        // คนที่ส่ง
        Optional<String> opt = SecurityUtil.getCurrentUserId();
        // ถ้าไม่พบ user
        if (opt.isEmpty()) {
            throw ChatException.accessDenied();
        }

        // TODO: validate message

        final String destination = "chat";

        ChatMessageResponse payload = new ChatMessageResponse();
        payload.setFrom(opt.get());
        payload.setMessage(request.getMessage());

        template.convertAndSend(destination, payload);
    }
}
