package com.corp.borala.repository;

import com.corp.borala.model.Favorita;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoritaRepository extends JpaRepository<Favorita, Long> {

    List<Favorita> findByUsuarioIdOrderByRestauranteNomeAsc(Long usuarioId);

    Optional<Favorita> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Favorita> findByUsuarioIdAndRestauranteId(Long usuarioId, Long restauranteId);

    void deleteByUsuarioId(Long usuarioId);

    void deleteByRestauranteId(Long restauranteId);

    /** Ids dos restaurantes mais favoritados, do mais para o menos favoritado. */
    @Query("""
            select f.restaurante.id from Favorita f
            group by f.restaurante.id
            order by count(f) desc
            """)
    List<Long> idsMaisFavoritados(Pageable limite);
}
