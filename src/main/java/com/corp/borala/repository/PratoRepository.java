package com.corp.borala.repository;

import com.corp.borala.model.Prato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PratoRepository extends JpaRepository<Prato, Long> {

    /** Pratos do cardápio de um restaurante, filtrando pelo nome (texto vazio = todos). */
    @Query("""
            select p from Prato p
            where p.categoria.cardapio.restaurante.id = :restauranteId
              and (:busca = '' or lower(p.nome) like lower(concat('%', :busca, '%')))
            order by p.nome
            """)
    List<Prato> buscarPorRestaurante(@Param("restauranteId") Long restauranteId, @Param("busca") String busca);

    /** Garante que o prato pertence ao restaurante informado. */
    @Query("""
            select p from Prato p
            where p.id = :pratoId
              and p.categoria.cardapio.restaurante.id = :restauranteId
            """)
    Optional<Prato> buscarPorIdDoRestaurante(@Param("pratoId") Long pratoId, @Param("restauranteId") Long restauranteId);

    /** Menor e maior preço entre os pratos do restaurante (uma linha; valores nulos se não houver pratos). */
    @Query("""
            select min(p.preco), max(p.preco) from Prato p
            where p.categoria.cardapio.restaurante.id = :restauranteId
            """)
    List<Object[]> calcularFaixaPreco(@Param("restauranteId") Long restauranteId);

    void deleteByCategoriaCardapioRestauranteId(Long restauranteId);
}
