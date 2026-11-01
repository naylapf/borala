package com.corp.borala.repository;

import com.corp.borala.model.Papel;
import com.corp.borala.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByOrderByNomeAsc();

    List<Usuario> findByPapelOrderByNomeAsc(Papel papel);
}
