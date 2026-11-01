package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Mídia (foto ou vídeo) exibida pelo restaurante (entidade fraca). Guarda só o endereço do arquivo. */
@Entity
@Table(name = "midia", uniqueConstraints = @UniqueConstraint(columnNames = {"restaurante_id", "url"}))
public class Midia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMidia tipo;

    protected Midia() {
        // exigido pelo JPA
    }

    public Midia(Restaurante restaurante, String url, TipoMidia tipo) {
        this.restaurante = restaurante;
        atualizar(url, tipo);
    }

    public void atualizar(String url, TipoMidia tipo) {
        this.url = url;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public String getUrl() {
        return url;
    }

    public TipoMidia getTipo() {
        return tipo;
    }
}
