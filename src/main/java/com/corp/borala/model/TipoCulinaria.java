package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

/** Tipo de culinária (ex.: Italiana, Brasileira). Usado por restaurantes e por pratos. */
@Entity
@Table(name = "tipo_culinaria")
public class TipoCulinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    protected TipoCulinaria() {
        // exigido pelo JPA
    }

    public TipoCulinaria(String nome) {
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

    // equals/hashCode pelo id: o restaurante guarda seus tipos em um Set e não pode duplicar.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoCulinaria outro)) {
            return false;
        }
        return id != null && id.equals(outro.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
