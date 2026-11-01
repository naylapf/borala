package com.corp.borala.repository;

import com.corp.borala.model.BuscaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuscaUsuarioRepository extends JpaRepository<BuscaUsuario, Long> {

    List<BuscaUsuario> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);

    Optional<BuscaUsuario> findByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
