package com.example.manager.dto;

import java.util.Set;

public record CrackHashStatusResponse(
        String status,
        Set<String> data
) {}