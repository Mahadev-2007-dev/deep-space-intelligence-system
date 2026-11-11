package org.deepspace.dsis.repository;

import org.deepspace.dsis.model.EmergencyIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyIncidentRepository extends JpaRepository<EmergencyIncident, Long> {
    List<EmergencyIncident> findAllByOrderByTriggeredAtDesc();
    List<EmergencyIncident> findByStatus(String status);
}
