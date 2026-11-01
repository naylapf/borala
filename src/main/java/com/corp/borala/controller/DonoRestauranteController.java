package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.RestauranteForm;
import com.corp.borala.security.UsuarioLogado;
import com.corp.borala.service.RestauranteService;
import com.corp.borala.service.TipoCulinariaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** O dono cadastra mais um restaurante para si, sem depender do ADMIN. */
@Controller
@RequestMapping("/dono/restaurantes")
public class DonoRestauranteController {

    private final RestauranteService restauranteService;
    private final TipoCulinariaService tipoCulinariaService;

    public DonoRestauranteController(RestauranteService restauranteService, TipoCulinariaService tipoCulinariaService) {
        this.restauranteService = restauranteService;
        this.tipoCulinariaService = tipoCulinariaService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new RestauranteForm());
        return paginaDoFormulario(model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") RestauranteForm form, BindingResult resultado,
                         @AuthenticationPrincipal UsuarioLogado usuario,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                restauranteService.criarParaDono(form, usuario.getId());
                redirect.addFlashAttribute("mensagem",
                        "Restaurante cadastrado! Ele aparece como pendente até o administrador verificar.");
                return "redirect:/dono";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(model);
    }

    private String paginaDoFormulario(Model model) {
        model.addAttribute("tiposCulinaria", tipoCulinariaService.listar());
        model.addAttribute("acao", "/dono/restaurantes");
        return "restaurantes/formulario";
    }
}
