package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.CadastroClienteForm;
import com.corp.borala.form.CadastroDonoForm;
import com.corp.borala.service.CadastroService;
import com.corp.borala.service.TipoCulinariaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Cadastros públicos: cliente e dono (que informa o restaurante no mesmo formulário). */
@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private final CadastroService cadastroService;
    private final TipoCulinariaService tipoCulinariaService;

    public CadastroController(CadastroService cadastroService, TipoCulinariaService tipoCulinariaService) {
        this.cadastroService = cadastroService;
        this.tipoCulinariaService = tipoCulinariaService;
    }

    @GetMapping
    public String escolher() {
        return "cadastro/escolha";
    }

    @GetMapping("/cliente")
    public String novoCliente(Model model) {
        model.addAttribute("form", new CadastroClienteForm());
        return "cadastro/cliente";
    }

    @PostMapping("/cliente")
    public String salvarCliente(@Valid @ModelAttribute("form") CadastroClienteForm form, BindingResult resultado) {
        if (!resultado.hasErrors()) {
            try {
                cadastroService.cadastrarCliente(form);
                return "redirect:/login?cadastrado";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return "cadastro/cliente";
    }

    @GetMapping("/dono")
    public String novoDono(Model model) {
        model.addAttribute("form", new CadastroDonoForm());
        return paginaDoDono(model);
    }

    @PostMapping("/dono")
    public String salvarDono(@Valid @ModelAttribute("form") CadastroDonoForm form, BindingResult resultado,
                             Model model) {
        if (!resultado.hasErrors()) {
            try {
                cadastroService.cadastrarDono(form);
                return "redirect:/login?cadastrado";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoDono(model);
    }

    private String paginaDoDono(Model model) {
        model.addAttribute("tiposCulinaria", tipoCulinariaService.listar());
        return "cadastro/dono";
    }
}
