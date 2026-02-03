package com.travelapp.controller;

import com.travelapp.model.BookingWorkflow;
import com.travelapp.model.WorkflowStep;
import com.travelapp.repository.BookingWorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "http://localhost:5173") // Allow access from React app
public class WorkflowController {

    @Autowired
    private BookingWorkflowRepository repository;

    @GetMapping
    public List<BookingWorkflow> getAllWorkflows() {
        return repository.findAll();
    }

    @Autowired
    private com.travelapp.service.WorkflowParserService parserService;

    @Autowired
    private com.travelapp.service.AdvisoryImpactService impactService;

    @PostMapping("/parse")
    public BookingWorkflow parseWorkflow(@RequestBody String plainText) {
        com.travelapp.model.ParsedItinerary result = parserService.parseItinerary(plainText);

        BookingWorkflow workflow = new BookingWorkflow();
        workflow.setCustomerName(result.getTitle());
        workflow.setCustomerEmail(result.getCustomerEmail());
        workflow.setSteps(result.getSteps());
        workflow.setTravelDate(result.getTravelDate());
        workflow.setSource(result.getSource());
        workflow.setDestination(result.getDestination());
        workflow.setFinished(false);

        // Validate immediately so warnings are persisted
        advisoryService.validateWorkflow(workflow);

        return repository.save(workflow);
    }

    @PostMapping
    public BookingWorkflow createWorkflow(@RequestBody BookingWorkflow workflow) {
        // Initial setup if steps are empty
        if (workflow.getSteps() == null || workflow.getSteps().isEmpty()) {
            List<WorkflowStep> steps = new ArrayList<>();
            steps.add(
                    new WorkflowStep("1", "Flight Selection", "Select flight", WorkflowStep.StepStatus.PENDING, false,
                            new java.util.HashMap<>()));
            steps.add(new WorkflowStep("2", "Hotel Booking", "Select hotel", WorkflowStep.StepStatus.PENDING, false,
                    new java.util.HashMap<>()));
            steps.add(new WorkflowStep("3", "Payment", "Complete payment", WorkflowStep.StepStatus.PENDING, false,
                    new java.util.HashMap<>()));
            workflow.setSteps(steps);
        }

        advisoryService.validateWorkflow(workflow);
        return repository.save(workflow);
    }

    @PutMapping("/{id}")
    public BookingWorkflow updateWorkflow(@PathVariable String id, @RequestBody BookingWorkflow workflow) {
        return repository.save(workflow);
    }

    @Autowired
    private com.travelapp.service.AdvisoryService advisoryService;

    @GetMapping("/{id}")
    public BookingWorkflow getWorkflow(@PathVariable String id) {
        BookingWorkflow workflow = repository.findById(id).orElse(null);
        if (workflow != null && !workflow.isFinished()) {
            // Validate and SAVE if new warnings found
            // This ensures subsequent polls don't need to re-run expensive LLM if nothing
            // changed
            boolean changed = advisoryService.validateWorkflow(workflow);
            if (changed) {
                repository.save(workflow);
            }
        }
        return workflow;
    }

    @PostMapping("/{id}/response")
    public String handleCustomerResponse(@PathVariable String id, @RequestBody String response) {
        return impactService.handleCustomerResponse(id, response);
    }
}
