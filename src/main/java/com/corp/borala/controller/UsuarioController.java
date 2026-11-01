package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.UsuarioForm;
import com.corp.borala.model.Papel;
import com.corp.borala.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Telas do CRUD de usuários. */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new UsuarioForm());
        return paginaDoFormulario(model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", usuarioService.montarFormulario(id));
        return paginaDoFormulario(model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") UsuarioForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                usuarioService.salvar(form);
                redirect.addFlashAttribute("mensagem", "Usuário salvo com sucesso.");
                return "redirect:/usuarios";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            usuarioService.excluir(id);
            redirect.addFlashAttribute("mensagem", "Usuário excluído.");
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    private String paginaDoFormulario(Model model) {
        model.addAttribute("papeis", Papel.values());
        return "usuarios/formulario";
    }
}
