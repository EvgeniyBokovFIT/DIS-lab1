package com.example.manager.controller;

import com.example.manager.service.ManagerService;
import generated.CrackHashWorkerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/manager")
@RequiredArgsConstructor
public class ManagerInternalController {

    private final ManagerService managerService;

    @PatchMapping("/hash/crack/request")
    public ResponseEntity<?> collectAnswers(@RequestBody CrackHashWorkerResponse crackHashWorkerResponse){
        managerService.addAnswersToEntity(crackHashWorkerResponse);
        return ResponseEntity.ok().build();
    }
}
