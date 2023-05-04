package com.example.worker.controller;

import com.example.worker.service.WorkerService;
import generated.ManagerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/api/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping(value = "/hash/crack/task", consumes = { MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<?> crackHash(@RequestBody ManagerRequest request) {
        workerService.findWords(request);
        return ResponseEntity.ok().build();
    }
}
