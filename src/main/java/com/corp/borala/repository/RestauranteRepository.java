package com.corp.borala.repository;

import com.corp.borala.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long>, JpaSpecificationExecutor<Restaurante> {

    Optional<Restaurante> findByCnpj(String cnpj);

    /** Restaurantes gerenciados por um usuário (tela do dono). */
    List<Restaurante> findByDonoIdOrderByNomeAsc(Long donoId);

    /** Usado na checagem de permissão: o restaurante pertence a este dono? */
    boolean existsByIdAndDonoId(Long id, Long donoId);
}
