package com.hospital.repository;

import com.hospital.model.AtencionResumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtencionResumenRepository extends JpaRepository<AtencionResumen, Long> {
    List<AtencionResumen> findByPacienteIdOrderByFechaAtencionDesc(Long pacienteId);

    Optional<AtencionResumen> findByIdAndPacienteId(Long id, Long pacienteId);
}
