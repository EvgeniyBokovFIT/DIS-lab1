package com.example.manager.controller;

import com.example.manager.service.ManagerService;
import com.example.manager.dto.CrackHashRequest;
import com.example.manager.dto.CrackHashRequestResponse;
import com.example.manager.dto.CrackHashStatusResponse;
import generated.CrackHashWorkerResponse;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hash")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/crack")
    public ResponseEntity<CrackHashRequestResponse> crackHash(@RequestBody CrackHashRequest request) {
        CrackHashRequestResponse response = managerService.startCracking(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<CrackHashStatusResponse> getStatus(@RequestParam String requestId) {
        CrackHashStatusResponse crackHashStatusResponse = managerService.getStatus(requestId);
        return ResponseEntity.ok(crackHashStatusResponse);
    }
}
