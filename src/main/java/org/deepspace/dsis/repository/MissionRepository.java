package org.deepspace.dsis.repository;

import org.deepspace.dsis.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByStatus(String status);
    List<Mission> findAllByOrderByCreatedAtDesc();
}
