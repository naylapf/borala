package com.corp.borala.repository;

import com.corp.borala.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByCardapioRestauranteIdOrderByNomeAsc(Long restauranteId);

    Optional<Categoria> findByIdAndCardapioRestauranteId(Long id, Long restauranteId);

    Optional<Categoria> findByCardapioIdAndNomeIgnoreCase(Long cardapioId, String nome);

    void deleteByCardapioRestauranteId(Long restauranteId);
}
