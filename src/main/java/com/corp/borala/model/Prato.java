package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Prato de um cardápio. Pertence a uma categoria e a um tipo de culinária. */
@Entity
@Table(name = "prato")
public class Prato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /** Indica se o prato está disponível no momento. */
    @Column(nullable = false)
    private boolean disponibilidade;

    /** Quantas pessoas o prato serve. */
    @Column(nullable = false)
    private Integer rendimento;

    /** Caminho ou URL da foto. Opcional por enquanto. */
    private String foto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_culinaria_id", nullable = false)
    private TipoCulinaria tipoCulinaria;

    protected Prato() {
        // exigido pelo JPA
    }

    public Prato(String nome, String descricao, BigDecimal preco, boolean disponibilidade,
                 Integer rendimento, String foto, Categoria categoria, TipoCulinaria tipoCulinaria) {
        atualizar(nome, descricao, preco, disponibilidade, rendimento, foto, categoria, tipoCulinaria);
    }

    /** Altera todos os dados editáveis do prato de uma só vez. */
    public void atualizar(String nome, String descricao, BigDecimal preco, boolean disponibilidade,
                          Integer rendimento, String foto, Categoria categoria, TipoCulinaria tipoCulinaria) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.disponibilidade = disponibilidade;
        this.rendimento = rendimento;
        this.foto = foto;
        this.categoria = categoria;
        this.tipoCulinaria = tipoCulinaria;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public boolean isDisponibilidade() {
        return disponibilidade;
    }

    public Integer getRendimento() {
        return rendimento;
    }

    public String getFoto() {
        return foto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public TipoCulinaria getTipoCulinaria() {
        return tipoCulinaria;
    }
}
