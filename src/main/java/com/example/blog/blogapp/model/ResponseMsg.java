package com.example.blog.blogapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class ResponseMsg {
    public ResponseMsg(LocalDateTime timeStamp, String message, String details) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    private LocalDateTime timeStamp;
    private String message;
    private String details;
}
