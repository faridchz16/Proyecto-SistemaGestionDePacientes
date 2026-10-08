package com.hospital.repository;

import com.hospital.model.Cita;
import com.hospital.model.EstadoCita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    @EntityGraph(attributePaths = {"paciente", "medico"})
    @Query("""
           select c from Cita c
           where (:desde is null or c.fechaHora >= :desde)
             and (:hasta is null or c.fechaHora < :hasta)
             and (:estado is null or c.estado = :estado)
             and (:medicoId is null or c.medico.id = :medicoId)
           order by c.fechaHora
           """)
    List<Cita> buscar(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
                      @Param("estado") EstadoCita estado, @Param("medicoId") Long medicoId);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    boolean existsByMedicoIdAndFechaHoraAndEstadoNotAndIdNot(Long medicoId, LocalDateTime fechaHora,
                                                             EstadoCita estado, Long id);
}
