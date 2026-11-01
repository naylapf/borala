package com.corp.borala.exception;

/** Erro de regra de negócio ligado a um campo de formulário (ex.: e-mail já cadastrado). */
public class CampoInvalidoException extends RegraDeNegocioException {

    private final String campo;

    public CampoInvalidoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    /** Nome do atributo do formulário onde o erro deve aparecer. */
    public String getCampo() {
        return campo;
    }
}
