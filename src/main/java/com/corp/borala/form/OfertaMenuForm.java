package com.corp.borala.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Formulário de oferta de menu (modalidade oferecida por um restaurante). */
public class OfertaMenuForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    @NotNull(message = "Selecione a modalidade de menu")
    private Long modalidadeMenuId;

    @NotNull(message = "Informe o valor")
    @DecimalMin(value = "0.00", message = "O valor não pode ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Valor inválido")
    private BigDecimal valor;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModalidadeMenuId() {
        return modalidadeMenuId;
    }

    public void setModalidadeMenuId(Long modalidadeMenuId) {
        this.modalidadeMenuId = modalidadeMenuId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
