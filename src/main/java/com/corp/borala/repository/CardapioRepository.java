package com.corp.borala.repository;

import com.corp.borala.model.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

    Optional<Cardapio> findByRestauranteId(Long restauranteId);

    void deleteByRestauranteId(Long restauranteId);
}
