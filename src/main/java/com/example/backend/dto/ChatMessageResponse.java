package com.example.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageResponse {

    private String from;

    private String message;

    private Date created;

    public ChatMessageResponse(){
        created = new Date();
    }
}
