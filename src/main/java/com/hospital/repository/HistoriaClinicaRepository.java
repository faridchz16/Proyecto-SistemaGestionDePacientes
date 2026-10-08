package com.hospital.repository;

import com.hospital.model.HistoriaClinica;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    @EntityGraph(attributePaths = "paciente")
    Optional<HistoriaClinica> findByPacienteId(Long pacienteId);

    boolean existsByPacienteId(Long pacienteId);
}
