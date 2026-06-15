package com.travelapp.controller;

import com.travelapp.service.AdvisoryImpactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
public class CustomerResponseController {

    @Autowired
    private AdvisoryImpactService impactService;

    @PostMapping("/{id}/customer-response")
    public String handleCustomerResponse(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String responseText = payload.get("response");
        if (responseText == null) {
            throw new IllegalArgumentException("Response text is required");
        }
        return impactService.handleCustomerResponse(id, responseText);
    }
}
