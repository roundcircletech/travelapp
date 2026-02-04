package com.travelapp.controller;

import com.travelapp.model.Advisory;
import com.travelapp.repository.AdvisoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advisories")
public class AdvisoryController {

    @Autowired
    private AdvisoryRepository repository;

    @Autowired
    private com.travelapp.service.AdvisoryImpactService impactService;

    @GetMapping
    public List<Advisory> getAllAdvisories() {
        return repository.findAll();
    }

    @PostMapping
    public Advisory createAdvisory(@RequestBody Advisory advisory) {
        Advisory saved = repository.save(advisory);
        impactService.processNewAdvisory(saved);
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteAdvisory(@PathVariable String id) {
        repository.deleteById(id);
    }
}
