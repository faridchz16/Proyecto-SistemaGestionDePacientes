package com.hospital.repository;

import com.hospital.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByRolId(Long rolId);

    List<Usuario> findByRolNombreAndActivoTrueOrderByApellidos(String rolNombre);

    List<Usuario> findAllByOrderByIdAsc();
}
