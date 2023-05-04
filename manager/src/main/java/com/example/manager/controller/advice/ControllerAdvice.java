package com.example.manager.controller.advice;

import com.example.manager.exception.CrackException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(CrackException.class)
    public ResponseEntity<String> crackExceptionHandler(CrackException crackException) {
        return ResponseEntity.badRequest().body(crackException.getMessage());
    }
}
