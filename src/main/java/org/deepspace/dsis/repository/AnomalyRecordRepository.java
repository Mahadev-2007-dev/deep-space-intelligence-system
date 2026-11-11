package org.deepspace.dsis.repository;

import org.deepspace.dsis.model.AnomalyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyRecordRepository extends JpaRepository<AnomalyRecord, Long> {
    List<AnomalyRecord> findTop100ByOrderByTimestampDesc();
    List<AnomalyRecord> findByStatus(String status);
    List<AnomalyRecord> findBySpacecraftId(Long spacecraftId);
    long countByStatus(String status);
}
