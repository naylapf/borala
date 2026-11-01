package com.corp.borala.repository;

import com.corp.borala.model.OfertaMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfertaMenuRepository extends JpaRepository<OfertaMenu, Long> {

    List<OfertaMenu> findByRestauranteIdOrderByModalidadeMenuNomeAsc(Long restauranteId);

    Optional<OfertaMenu> findByIdAndRestauranteId(Long id, Long restauranteId);

    Optional<OfertaMenu> findByRestauranteIdAndModalidadeMenuId(Long restauranteId, Long modalidadeMenuId);

    long countByRestauranteId(Long restauranteId);

    void deleteByRestauranteId(Long restauranteId);
}
