package com.corp.borala.repository;

import com.corp.borala.model.ModalidadeMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModalidadeMenuRepository extends JpaRepository<ModalidadeMenu, Long> {

    Optional<ModalidadeMenu> findByNomeIgnoreCase(String nome);

    List<ModalidadeMenu> findAllByOrderByNomeAsc();
}
