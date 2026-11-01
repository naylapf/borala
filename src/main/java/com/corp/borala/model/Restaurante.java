package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Restaurante, com CNPJ único. É gerenciado por um usuário (relacionamento Gerencia):
 * cada restaurante tem um dono, e um usuário pode gerenciar vários restaurantes.
 * A multiculinária do DER é derivada: sai da lista de tipos de culinária, não vira coluna.
 */
@Entity
@Table(name = "restaurante")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Apenas dígitos/letras, sem pontuação. */
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    /** Apenas dígitos (7). */
    @Column(nullable = false, length = 7)
    private String cnae;

    @Column(nullable = false, length = 100)
    private String nome;

    /** Apenas dígitos, com DDD. */
    @Column(nullable = false, length = 11)
    private String telefone;

    /** Indica se o cadastro do restaurante já foi verificado. */
    @Column(nullable = false)
    private boolean verificacao;

    @Embedded
    private Endereco endereco;

    @Embedded
    private FaixaPreco faixaPreco;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dono_id", nullable = false)
    private Usuario dono;

    /** Relacionamento "restaurante oferece tipo de culinária" (N:N). */
    @ManyToMany
    @JoinTable(
            name = "restaurante_tipo_culinaria",
            joinColumns = @JoinColumn(name = "restaurante_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_culinaria_id"))
    private Set<TipoCulinaria> tiposCulinaria = new HashSet<>();

    protected Restaurante() {
        // exigido pelo JPA
    }

    public Restaurante(String cnpj, String cnae, String nome, String telefone, boolean verificacao,
                       Endereco endereco, Usuario dono) {
        atualizar(cnpj, cnae, nome, telefone, verificacao, endereco, dono);
    }

    /** Altera os dados editáveis do restaurante de uma só vez (tipos de culinária e faixa de preço têm método próprio). */
    public void atualizar(String cnpj, String cnae, String nome, String telefone, boolean verificacao,
                          Endereco endereco, Usuario dono) {
        this.cnpj = cnpj;
        this.cnae = cnae;
        this.nome = nome;
        this.telefone = telefone;
        this.verificacao = verificacao;
        this.endereco = endereco;
        this.dono = dono;
    }

    /**
     * Grava a faixa de preço calculada a partir dos pratos.
     * Sem pratos (mínimo ou máximo nulos) o restaurante fica sem faixa.
     */
    public void definirFaixaPreco(BigDecimal minimo, BigDecimal maximo) {
        this.faixaPreco = (minimo == null || maximo == null) ? null : new FaixaPreco(minimo, maximo);
    }

    /** Troca a lista de tipos de culinária do restaurante. */
    public void definirTiposCulinaria(Collection<TipoCulinaria> tipos) {
        tiposCulinaria.clear();
        tiposCulinaria.addAll(tipos);
    }

    public void adicionarTipoCulinaria(TipoCulinaria tipo) {
        tiposCulinaria.add(tipo);
    }

    public void adicionarTiposCulinaria(Collection<TipoCulinaria> tipos) {
        tiposCulinaria.addAll(tipos);
    }

    public Long getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getCnae() {
        return cnae;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public boolean isVerificacao() {
        return verificacao;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public FaixaPreco getFaixaPreco() {
        return faixaPreco;
    }

    public Usuario getDono() {
        return dono;
    }

    public Set<TipoCulinaria> getTiposCulinaria() {
        return Collections.unmodifiableSet(tiposCulinaria);
    }
}
