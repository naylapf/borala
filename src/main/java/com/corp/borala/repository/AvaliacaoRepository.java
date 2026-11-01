package com.corp.borala.repository;

import com.corp.borala.model.Avaliacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByUsuarioIdOrderByDataDescIdDesc(Long usuarioId);

    Optional<Avaliacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Avaliacao> findByUsuarioIdAndRestauranteId(Long usuarioId, Long restauranteId);

    void deleteByUsuarioId(Long usuarioId);

    void deleteByRestauranteId(Long restauranteId);

    /** Avaliações de um restaurante, da mais recente para a mais antiga, já com o autor carregado. */
    @Query("""
            select a from Avaliacao a join fetch a.usuario
            where a.restaurante.id = :restauranteId
            order by a.data desc, a.id desc
            """)
    List<Avaliacao> listarDoRestaurante(@Param("restauranteId") Long restauranteId);

    /** Para cada restaurante: id, nota média (Double) e quantidade de avaliações (Long). */
    @Query("""
            select a.restaurante.id, avg(a.nota), count(a) from Avaliacao a
            where a.restaurante.id in :ids
            group by a.restaurante.id
            """)
    List<Object[]> resumoPorRestaurante(@Param("ids") Collection<Long> ids);

    /** Ids dos restaurantes mais bem avaliados: maior média primeiro, desempate por mais avaliações. */
    @Query("""
            select a.restaurante.id from Avaliacao a
            group by a.restaurante.id
            order by avg(a.nota) desc, count(a) desc
            """)
    List<Long> idsMelhorAvaliados(Pageable limite);
}
