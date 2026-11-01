package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Endereço do restaurante (atributo composto do DER).
 * Fica guardado nas colunas da própria tabela restaurante.
 */
@Embeddable
public class Endereco {

    /** Rua ou avenida. */
    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    /** Sigla do estado, ex.: MS. */
    @Column(nullable = false, length = 2)
    private String uf;

    /** Apenas dígitos. */
    @Column(nullable = false, length = 8)
    private String cep;

    /** Opcional. */
    private String complemento;

    protected Endereco() {
        // exigido pelo JPA
    }

    public Endereco(String logradouro, String bairro, String cidade, String uf, String cep, String complemento) {
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.complemento = complemento;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public String getCep() {
        return cep;
    }

    public String getComplemento() {
        return complemento;
    }
}
