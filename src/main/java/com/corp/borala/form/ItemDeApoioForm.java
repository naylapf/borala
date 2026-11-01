package com.corp.borala.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Formulário das tabelas de apoio que só têm nome (tipo de culinária e modalidade de menu). */
public class ItemDeApoioForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
