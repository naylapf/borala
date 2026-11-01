package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Categoria de pratos dentro de um cardápio (ex.: Massas, Sobremesas). */
@Entity
@Table(name = "categoria", uniqueConstraints = @UniqueConstraint(columnNames = {"cardapio_id", "nome"}))
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cardapio_id", nullable = false)
    private Cardapio cardapio;

    protected Categoria() {
        // exigido pelo JPA
    }

    public Categoria(String nome, Cardapio cardapio) {
        this.nome = nome;
        this.cardapio = cardapio;
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

    public Cardapio getCardapio() {
        return cardapio;
    }
}
