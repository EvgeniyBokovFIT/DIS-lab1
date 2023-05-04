package com.example.manager.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CrackEntity {
    RequestStatus requestStatus = RequestStatus.IN_PROGRESS;
    LocalTime requestTime;
    Set<String> results = new HashSet<>();
    Integer workersAnswered = 0;
}
