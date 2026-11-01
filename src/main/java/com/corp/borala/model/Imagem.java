package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Imagem enviada por um usuário (foto de prato ou de restaurante), guardada no próprio banco. */
@Entity
@Table(name = "imagem")
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_conteudo", nullable = false, length = 30)
    private String tipoConteudo;

    @Column(nullable = false)
    private byte[] dados;

    protected Imagem() {
        // exigido pelo JPA
    }

    public Imagem(String tipoConteudo, byte[] dados) {
        this.tipoConteudo = tipoConteudo;
        this.dados = dados;
    }

    public Long getId() {
        return id;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public byte[] getDados() {
        return dados;
    }
}
