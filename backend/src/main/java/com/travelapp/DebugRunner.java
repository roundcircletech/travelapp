package com.travelapp;

import com.travelapp.model.BookingWorkflow;
import com.travelapp.repository.BookingWorkflowRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DebugRunner implements CommandLineRunner {

    private final BookingWorkflowRepository repository;

    public DebugRunner(BookingWorkflowRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== DEBUG RUNNER START ==========");

        System.out.println("Listing all workflows:");
        repository.findAll().forEach(w -> System.out.println("Found: " + w.getId() + " - " + w.getCustomerName()));

        String testId = "83da7179-4f21-4780-83b1-9e6e587cecc4"; // ID from previous list
        System.out.println("Attempting to findById: " + testId);
        Optional<BookingWorkflow> result = repository.findById(testId);

        if (result.isPresent()) {
            System.out.println("SUCCESS: Found workflow via findById: " + result.get().getCustomerName());
        } else {
            System.out.println("FAILURE: Could not find workflow via findById");
        }

        System.out.println("========== DEBUG RUNNER END ==========");
    }
}
