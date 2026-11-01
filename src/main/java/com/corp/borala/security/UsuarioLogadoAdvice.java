package com.corp.borala.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Deixa o usuário logado disponível em todas as telas como ${usuarioLogado} (nulo se ninguém entrou). */
@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("usuarioLogado")
    public UsuarioLogado usuarioLogado(@AuthenticationPrincipal UsuarioLogado usuario) {
        return usuario;
    }
}
