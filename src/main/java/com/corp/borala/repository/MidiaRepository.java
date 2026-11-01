package com.corp.borala.repository;

import com.corp.borala.model.Midia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MidiaRepository extends JpaRepository<Midia, Long> {

    List<Midia> findByRestauranteIdOrderByIdAsc(Long restauranteId);

    Optional<Midia> findByIdAndRestauranteId(Long id, Long restauranteId);

    Optional<Midia> findByRestauranteIdAndUrl(Long restauranteId, String url);

    void deleteByRestauranteId(Long restauranteId);

    /** Fotos (não vídeos) dos restaurantes informados, da mais antiga para a mais nova. */
    @Query("""
            select m from Midia m
            where m.restaurante.id in :ids and m.tipo = com.corp.borala.model.TipoMidia.FOTO
            order by m.id
            """)
    List<Midia> fotosDosRestaurantes(@Param("ids") Collection<Long> ids);
}
