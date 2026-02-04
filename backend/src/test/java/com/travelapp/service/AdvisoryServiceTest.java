package com.travelapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.model.Advisory;
import com.travelapp.model.BookingWorkflow;
import com.travelapp.model.WorkflowStep;
import com.travelapp.repository.AdvisoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AdvisoryServiceTest {

    @Mock
    private AdvisoryRepository repository;

    @Mock
    private LlmService llmService;

    @InjectMocks
    private AdvisoryService advisoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(advisoryService, "objectMapper", objectMapper);
    }

    @Test
    void validateWorkflow_ShouldUpdateMultipleSteps_WhenViolationsFound() {
        // Arrange
        Advisory advisory = new Advisory("India", "China", "HIGH", "Direct Flight Ban");
        advisory.setId("1");
        when(repository.findAll()).thenReturn(Collections.singletonList(advisory));

        WorkflowStep step1 = new WorkflowStep("step1", "Flight Delhi to Guangzhou", "Book flight", WorkflowStep.StepStatus.PENDING, false, new HashMap<>());
        WorkflowStep step2 = new WorkflowStep("step2", "Hotel in Guangzhou", "Book hotel", WorkflowStep.StepStatus.PENDING, false, new HashMap<>());

        BookingWorkflow workflow = new BookingWorkflow();
        workflow.setId("wf1");
        workflow.setAgentId("User1");
        workflow.setSource("Delhi");
        workflow.setDestination("Guangzhou");
        workflow.setSteps(Arrays.asList(step1, step2));

        String mockLlmResponse = "```json\n"
                + "{\n"
                + "  \"step1\": {\n"
                + "    \"warning\": \"Direct flights banned.\",\n"
                + "    \"alternative\": \"Fly via connecting hub.\"\n"
                + "  },\n"
                + "  \"step2\": {\n"
                + "    \"warning\": \"Travel caution in region.\",\n"
                + "    \"alternative\": \"Ensure refundable booking.\"\n"
                + "  }\n"
                + "}\n"
                + "```";

        when(llmService.fetchLlmResponse(anyString())).thenReturn(mockLlmResponse);

        // Act
        boolean result = advisoryService.validateWorkflow(workflow);

        // Assert
        assertTrue(result, "Should return true if workflow was updated");

        assertEquals("Direct flights banned.", step1.getWarning());
        assertEquals("Fly via connecting hub.", step1.getAlternative());

        assertEquals("Travel caution in region.", step2.getWarning());
        assertEquals("Ensure refundable booking.", step2.getAlternative());
    }
}
