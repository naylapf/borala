package com.corp.borala.controller;

import com.corp.borala.security.UsuarioLogado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Tela de login (o envio do formulário e o logout são tratados pelo Spring Security). */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal UsuarioLogado usuario) {
        // quem já entrou não precisa ver o login de novo
        return usuario != null ? "redirect:/painel" : "login";
    }
}
