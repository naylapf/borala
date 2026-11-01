package com.corp.borala.controller;

import com.corp.borala.security.UsuarioLogado;
import com.corp.borala.service.RestauranteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Leva cada papel para a sua tela inicial e mostra o painel do dono. */
@Controller
public class PainelController {

    private final RestauranteService restauranteService;

    public PainelController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    /** Destino do login e dos links "voltar": ADMIN vai para a lista de restaurantes, DONO para o painel, CLIENTE para a busca. */
    @GetMapping("/painel")
    public String painel(@AuthenticationPrincipal UsuarioLogado usuario) {
        if (usuario.isAdmin()) {
            return "redirect:/restaurantes";
        }
        if (usuario.isDono()) {
            return "redirect:/dono";
        }
        return "redirect:/";
    }

    /** Restaurantes que o dono gerencia, com atalhos para pratos, horários, mídias etc. */
    @GetMapping("/dono")
    public String painelDoDono(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("restaurantes", restauranteService.listarDoDono(usuario.getId()));
        return "dono/painel";
    }
}
