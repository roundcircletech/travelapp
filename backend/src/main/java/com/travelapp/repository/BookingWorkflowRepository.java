package com.travelapp.repository;

import com.travelapp.model.BookingWorkflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingWorkflowRepository extends MongoRepository<BookingWorkflow, String> {

    java.util.List<BookingWorkflow> findByTravelDateAfter(java.time.LocalDate date);
}
