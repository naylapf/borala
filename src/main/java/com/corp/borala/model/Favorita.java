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

import java.time.LocalDate;

/** Relacionamento "Favorita" do DER: restaurante marcado como favorito por um usuário. */
@Entity
@Table(name = "favorita", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "restaurante_id"}))
public class Favorita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    /** Dia em que o restaurante foi favoritado. */
    @Column(name = "data", nullable = false)
    private LocalDate data;

    protected Favorita() {
        // exigido pelo JPA
    }

    public Favorita(Usuario usuario, Restaurante restaurante, LocalDate data) {
        this.usuario = usuario;
        this.restaurante = restaurante;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public LocalDate getData() {
        return data;
    }
}
