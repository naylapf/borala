package com.corp.borala.repository;

import com.corp.borala.model.DiaSemana;
import com.corp.borala.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByRestauranteId(Long restauranteId);

    Optional<Horario> findByIdAndRestauranteId(Long id, Long restauranteId);

    Optional<Horario> findByRestauranteIdAndDiaSemanaAndAbertura(Long restauranteId, DiaSemana diaSemana, LocalTime abertura);

    void deleteByRestauranteId(Long restauranteId);
}
