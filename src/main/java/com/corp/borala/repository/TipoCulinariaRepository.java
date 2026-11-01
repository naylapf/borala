package com.corp.borala.repository;

import com.corp.borala.model.TipoCulinaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoCulinariaRepository extends JpaRepository<TipoCulinaria, Long> {

    Optional<TipoCulinaria> findByNomeIgnoreCase(String nome);

    List<TipoCulinaria> findAllByOrderByNomeAsc();
}
