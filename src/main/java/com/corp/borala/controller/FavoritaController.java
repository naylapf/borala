package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.FavoritaForm;
import com.corp.borala.service.FavoritaService;
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

/** Tela dos restaurantes favoritos de um usuário. A lista e o formulário de adicionar ficam na mesma página. */
@Controller
@RequestMapping("/usuarios/{usuarioId}/favoritas")
public class FavoritaController {

    private final FavoritaService favoritaService;
    private final UsuarioService usuarioService;
    private final RestauranteService restauranteService;

    public FavoritaController(FavoritaService favoritaService,
                              UsuarioService usuarioService,
                              RestauranteService restauranteService) {
        this.favoritaService = favoritaService;
        this.usuarioService = usuarioService;
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public String listar(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("form", new FavoritaForm());
        return paginaDaLista(usuarioId, model);
    }

    @PostMapping
    public String adicionar(@PathVariable Long usuarioId,
                            @Valid @ModelAttribute("form") FavoritaForm form, BindingResult resultado,
                            Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                favoritaService.adicionar(usuarioId, form);
                redirect.addFlashAttribute("mensagem", "Restaurante adicionado aos favoritos.");
                return "redirect:/usuarios/" + usuarioId + "/favoritas";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDaLista(usuarioId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long usuarioId, @PathVariable Long id, RedirectAttributes redirect) {
        favoritaService.excluir(usuarioId, id);
        redirect.addFlashAttribute("mensagem", "Restaurante removido dos favoritos.");
        return "redirect:/usuarios/" + usuarioId + "/favoritas";
    }

    private String paginaDaLista(Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(usuarioId));
        model.addAttribute("favoritas", favoritaService.listar(usuarioId));
        model.addAttribute("restaurantes", restauranteService.listarTodos());
        return "favoritas/lista";
    }
}
