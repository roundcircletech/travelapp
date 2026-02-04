package com.travelapp.service;

import com.travelapp.model.Advisory;
import com.travelapp.model.BookingWorkflow;
import com.travelapp.model.WorkflowStep;
import com.travelapp.repository.BookingWorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AdvisoryImpactServiceTest {

    @Mock
    private BookingWorkflowRepository workflowRepository;

    @Mock
    private WorkflowCloneService cloneService;

    @Mock
    private AdvisoryService advisoryService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AdvisoryImpactService impactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processNewAdvisory_ShouldCloneAndAnnotate_WhenBookingIsImpacted() {
        // Arrange
        Advisory advisory = new Advisory("India", "China", "HIGH", "Ban");
        BookingWorkflow existingBooking = new BookingWorkflow();
        existingBooking.setId("wf-1");
        existingBooking.setTravelDate(LocalDate.now().plusDays(5));
        existingBooking.setCustomerName("John Doe");

        BookingWorkflow clonedDraft = new BookingWorkflow();
        clonedDraft.setId("draft-1");
        clonedDraft.setSteps(new java.util.ArrayList<>()); // Empty steps init

        when(workflowRepository.findByTravelDateAfter(any(LocalDate.class)))
                .thenReturn(Collections.singletonList(existingBooking));

        when(advisoryService.checkAdvisoryImpact(existingBooking, advisory)).thenReturn(true);
        when(cloneService.cloneForAdvisoryReview(existingBooking)).thenReturn(clonedDraft);
        when(advisoryService.generateAgentAdvisoryScript(existingBooking, advisory)).thenReturn("Call the customer.");

        // Act
        impactService.processNewAdvisory(advisory);

        // Assert
        // 1. Verify repository queried for future bookings
        verify(workflowRepository).findByTravelDateAfter(any(LocalDate.class));

        // 2. Verify cloning happened
        verify(cloneService).cloneForAdvisoryReview(existingBooking);

        // 3. Verify script generation
        verify(advisoryService).generateAgentAdvisoryScript(existingBooking, advisory);

        // 4. Verify save was called with modified draft
        ArgumentCaptor<BookingWorkflow> captor = ArgumentCaptor.forClass(BookingWorkflow.class);
        verify(workflowRepository).save(captor.capture());

        BookingWorkflow savedDraft = captor.getValue();
        assertEquals("draft-1", savedDraft.getId());
        assertEquals("John Doe (Advisory Review)", savedDraft.getCustomerName());
        assertFalse(savedDraft.getSteps().isEmpty());
        assertEquals("Advisory Impact Review", savedDraft.getSteps().get(0).getName());
        assertEquals("Call the customer.", savedDraft.getSteps().get(0).getMetadata().get("agentScript"));
    }

    @Test
    void processNewAdvisory_ShouldDoNothing_WhenBookingNotImpacted() {
        // Arrange
        Advisory advisory = new Advisory("France", "Germany", "LOW", "Rain");
        BookingWorkflow existingBooking = new BookingWorkflow();

        when(workflowRepository.findByTravelDateAfter(any(LocalDate.class)))
                .thenReturn(Collections.singletonList(existingBooking));

        when(advisoryService.checkAdvisoryImpact(existingBooking, advisory)).thenReturn(false);

        // Act
        impactService.processNewAdvisory(advisory);

        // Assert
        verify(cloneService, never()).cloneForAdvisoryReview(any());
        verify(workflowRepository, never()).save(any());
    }
}
