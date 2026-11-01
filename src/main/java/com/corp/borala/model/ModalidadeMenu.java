package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Modalidade de menu (ex.: À la carte, Rodízio). Os restaurantes a oferecem por meio da OfertaMenu. */
@Entity
@Table(name = "modalidade_menu")
public class ModalidadeMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    protected ModalidadeMenu() {
        // exigido pelo JPA
    }

    public ModalidadeMenu(String nome) {
        this.nome = nome;
    }

    public void atualizar(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
