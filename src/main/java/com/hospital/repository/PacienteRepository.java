package com.hospital.repository;

import com.hospital.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    @Query("""
           select p from Paciente p
           where :q is null or :q = ''
              or p.numeroDocumento like concat('%', :q, '%')
              or lower(concat(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno)) like lower(concat('%', :q, '%'))
           order by p.apellidoPaterno, p.apellidoMaterno, p.nombres
           """)
    List<Paciente> buscar(@Param("q") String q);
}
