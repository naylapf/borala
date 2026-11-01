package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.AvaliacaoForm;
import com.corp.borala.service.AvaliacaoService;
import com.corp.borala.service.RestauranteService;
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

/** Telas do CRUD das avaliações de restaurantes feitas por um usuário. */
@Controller
@RequestMapping("/usuarios/{usuarioId}/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final UsuarioService usuarioService;
    private final RestauranteService restauranteService;

    public AvaliacaoController(AvaliacaoService avaliacaoService,
                               UsuarioService usuarioService,
                               RestauranteService restauranteService) {
        this.avaliacaoService = avaliacaoService;
        this.usuarioService = usuarioService;
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public String listar(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(usuarioId));
        model.addAttribute("avaliacoes", avaliacaoService.listar(usuarioId));
        return "avaliacoes/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("form", new AvaliacaoForm());
        return paginaDoFormulario(usuarioId, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long usuarioId, @PathVariable Long id, Model model) {
        model.addAttribute("form", avaliacaoService.montarFormulario(usuarioId, id));
        return paginaDoFormulario(usuarioId, model);
    }

    @PostMapping
    public String salvar(@PathVariable Long usuarioId,
                         @Valid @ModelAttribute("form") AvaliacaoForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                avaliacaoService.salvar(usuarioId, form);
                redirect.addFlashAttribute("mensagem", "Avaliação salva com sucesso.");
                return "redirect:/usuarios/" + usuarioId + "/avaliacoes";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(usuarioId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long usuarioId, @PathVariable Long id, RedirectAttributes redirect) {
        avaliacaoService.excluir(usuarioId, id);
        redirect.addFlashAttribute("mensagem", "Avaliação excluída.");
        return "redirect:/usuarios/" + usuarioId + "/avaliacoes";
    }

    private String paginaDoFormulario(Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(usuarioId));
        model.addAttribute("restaurantes", restauranteService.listarTodos());
        return "avaliacoes/formulario";
    }
}
