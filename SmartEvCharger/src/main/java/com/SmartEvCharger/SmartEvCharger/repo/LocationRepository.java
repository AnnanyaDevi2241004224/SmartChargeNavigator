package com.SmartEvCharger.SmartEvCharger.repo;

import com.SmartEvCharger.SmartEvCharger.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Additional query methods can go here if needed
}

