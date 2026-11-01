package com.corp.borala.form;

import jakarta.validation.constraints.Size;

/** Formulário para registrar uma busca no histórico do usuário. */
public class BuscaUsuarioForm {

    @Size(max = 100, message = "A palavra-chave deve ter no máximo 100 caracteres")
    private String palavraChave;

    @Size(max = 500, message = "Os filtros devem ter no máximo 500 caracteres")
    private String filtros;

    public String getPalavraChave() {
        return palavraChave;
    }

    public void setPalavraChave(String palavraChave) {
        this.palavraChave = palavraChave;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
