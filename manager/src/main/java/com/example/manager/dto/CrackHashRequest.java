package com.example.manager.dto;

public record CrackHashRequest(
        String hash,
        int maxLength
) {}

