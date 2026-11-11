package org.deepspace.dsis.controller;

import org.deepspace.dsis.service.OrbitAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orbit")
public class OrbitController {

    private final OrbitAiService orbitAiService;

    public OrbitController(OrbitAiService orbitAiService) {
        this.orbitAiService = orbitAiService;
    }

    public static class ChatRequest {
        public String prompt;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chatWithOrbit(@RequestBody ChatRequest req) {
        String prompt = (req != null && req.prompt != null) ? req.prompt : "";
        Map<String, Object> response = orbitAiService.askOrbit(prompt);
        return ResponseEntity.ok(response);
    }
}
