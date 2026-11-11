package org.deepspace.dsis.repository;

import org.deepspace.dsis.model.Spacecraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpacecraftRepository extends JpaRepository<Spacecraft, Long> {
    Optional<Spacecraft> findByName(String name);
}
