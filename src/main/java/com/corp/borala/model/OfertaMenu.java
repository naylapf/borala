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

import java.math.BigDecimal;

/**
 * Oferta de menu: uma modalidade de menu oferecida por um restaurante, com valor e descrição próprios
 * (entidade associativa entre Restaurante e ModalidadeMenu). Os pratos que a compõem ficam em Composicao.
 */
@Entity
@Table(name = "oferta_menu", uniqueConstraints = @UniqueConstraint(columnNames = {"restaurante_id", "modalidade_menu_id"}))
public class OfertaMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modalidade_menu_id", nullable = false)
    private ModalidadeMenu modalidadeMenu;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /** Opcional (ex.: "rodízio de massas" ou "menu com pratos específicos"). */
    @Column(length = 500)
    private String descricao;

    protected OfertaMenu() {
        // exigido pelo JPA
    }

    public OfertaMenu(Restaurante restaurante, ModalidadeMenu modalidadeMenu, BigDecimal valor, String descricao) {
        this.restaurante = restaurante;
        atualizar(modalidadeMenu, valor, descricao);
    }

    public void atualizar(ModalidadeMenu modalidadeMenu, BigDecimal valor, String descricao) {
        this.modalidadeMenu = modalidadeMenu;
        this.valor = valor;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public ModalidadeMenu getModalidadeMenu() {
        return modalidadeMenu;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }
}
