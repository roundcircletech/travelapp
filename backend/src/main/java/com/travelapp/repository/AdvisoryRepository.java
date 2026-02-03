package com.travelapp.repository;

import com.travelapp.model.Advisory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AdvisoryRepository extends MongoRepository<Advisory, String> {
    // Find advisories that might be relevant (we'll filter smartly later or use
    // exact match)
    // For now, fetch all active or by country if needed
    List<Advisory> findBySourceCountryAndTargetCountry(String sourceCountry, String targetCountry);
}
