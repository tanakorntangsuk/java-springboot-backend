package com.example.backend.controller;

import com.example.backend.dto.ChatMessageRequest;
import com.example.backend.exception.BaseException;
import com.example.backend.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ResponseEntity<Void> post(@RequestBody ChatMessageRequest messageRequest) throws BaseException {
        chatService.post(messageRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
