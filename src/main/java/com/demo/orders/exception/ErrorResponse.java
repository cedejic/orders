package com.demo.orders.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ErrorResponse {

    String message;
    List<String> details;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
