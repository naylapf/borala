package com.corp.borala.form;

import com.corp.borala.model.FormaServico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Formulário para colocar um prato dentro de uma oferta de menu. */
public class ComposicaoForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    @NotNull(message = "Selecione o prato")
    private Long pratoId;

    @NotBlank(message = "Informe a cobrança")
    @Size(max = 50, message = "A cobrança deve ter no máximo 50 caracteres")
    private String cobranca;

    @NotNull(message = "Selecione a forma de serviço")
    private FormaServico formaServico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPratoId() {
        return pratoId;
    }

    public void setPratoId(Long pratoId) {
        this.pratoId = pratoId;
    }

    public String getCobranca() {
        return cobranca;
    }

    public void setCobranca(String cobranca) {
        this.cobranca = cobranca;
    }

    public FormaServico getFormaServico() {
        return formaServico;
    }

    public void setFormaServico(FormaServico formaServico) {
        this.formaServico = formaServico;
    }
}
