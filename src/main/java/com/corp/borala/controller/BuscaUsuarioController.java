package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.BuscaUsuarioForm;
import com.corp.borala.service.BuscaUsuarioService;
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

/** Tela do histórico de buscas de um usuário. A lista e o formulário de registrar ficam na mesma página. */
@Controller
@RequestMapping("/usuarios/{usuarioId}/buscas")
public class BuscaUsuarioController {

    private final BuscaUsuarioService buscaUsuarioService;
    private final UsuarioService usuarioService;

    public BuscaUsuarioController(BuscaUsuarioService buscaUsuarioService, UsuarioService usuarioService) {
        this.buscaUsuarioService = buscaUsuarioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("form", new BuscaUsuarioForm());
        return paginaDaLista(usuarioId, model);
    }

    @PostMapping
    public String registrar(@PathVariable Long usuarioId,
                            @Valid @ModelAttribute("form") BuscaUsuarioForm form, BindingResult resultado,
                            Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                buscaUsuarioService.registrar(usuarioId, form);
                redirect.addFlashAttribute("mensagem", "Busca registrada no histórico.");
                return "redirect:/usuarios/" + usuarioId + "/buscas";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDaLista(usuarioId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long usuarioId, @PathVariable Long id, RedirectAttributes redirect) {
        buscaUsuarioService.excluir(usuarioId, id);
        redirect.addFlashAttribute("mensagem", "Busca apagada do histórico.");
        return "redirect:/usuarios/" + usuarioId + "/buscas";
    }

    private String paginaDaLista(Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(usuarioId));
        model.addAttribute("buscas", buscaUsuarioService.listar(usuarioId));
        return "buscas/lista";
    }
}
