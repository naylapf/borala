package com.corp.borala.repository;

import com.corp.borala.model.Composicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComposicaoRepository extends JpaRepository<Composicao, Long> {

    List<Composicao> findByOfertaMenuIdOrderByPratoNomeAsc(Long ofertaMenuId);

    Optional<Composicao> findByIdAndOfertaMenuId(Long id, Long ofertaMenuId);

    Optional<Composicao> findByOfertaMenuIdAndPratoId(Long ofertaMenuId, Long pratoId);

    void deleteByOfertaMenuId(Long ofertaMenuId);

    void deleteByOfertaMenuRestauranteId(Long restauranteId);

    void deleteByPratoId(Long pratoId);
}
