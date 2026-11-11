package org.deepspace.dsis.repository;

import org.deepspace.dsis.model.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long> {
    List<TelemetryRecord> findTop50BySpacecraftIdOrderByTimestampDesc(Long spacecraftId);
    List<TelemetryRecord> findTop200ByOrderByTimestampDesc();
}
