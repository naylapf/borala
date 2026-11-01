package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Faixa de preço do restaurante (atributo composto do DER: mínimo e máximo).
 * Não é informada pelo restaurante: é calculada a partir dos pratos (menor e maior preço).
 * Restaurante sem pratos fica sem faixa (as duas colunas ficam vazias).
 */
@Embeddable
public class FaixaPreco {

    @Column(name = "faixa_preco_minimo", precision = 10, scale = 2)
    private BigDecimal minimo;

    @Column(name = "faixa_preco_maximo", precision = 10, scale = 2)
    private BigDecimal maximo;

    protected FaixaPreco() {
        // exigido pelo JPA
    }

    public FaixaPreco(BigDecimal minimo, BigDecimal maximo) {
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public BigDecimal getMinimo() {
        return minimo;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }
}
