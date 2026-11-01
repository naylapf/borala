package com.corp.borala.exception;

/** Erro de regra de negócio (ex.: excluir algo que está em uso). A mensagem pode ser mostrada ao usuário. */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
