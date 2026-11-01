package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.ItemDeApoioForm;
import com.corp.borala.model.Restaurante;
import com.corp.borala.service.CategoriaService;
import com.corp.borala.service.RestauranteService;
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

/** Telas do CRUD de categorias do cardápio. Usa as páginas genéricas da pasta templates/apoio. */
@Controller
@RequestMapping("/restaurantes/{restauranteId}/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final RestauranteService restauranteService;

    public CategoriaController(CategoriaService categoriaService, RestauranteService restauranteService) {
        this.categoriaService = categoriaService;
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public String listar(@PathVariable Long restauranteId, Model model) {
        prepararPagina(restauranteId, model);
        model.addAttribute("itens", categoriaService.listar(restauranteId));
        return "apoio/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long restauranteId, Model model) {
        prepararPagina(restauranteId, model);
        model.addAttribute("form", new ItemDeApoioForm());
        return "apoio/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long restauranteId, @PathVariable Long id, Model model) {
        prepararPagina(restauranteId, model);
        model.addAttribute("form", categoriaService.montarFormulario(restauranteId, id));
        return "apoio/formulario";
    }

    @PostMapping
    public String salvar(@PathVariable Long restauranteId,
                         @Valid @ModelAttribute("form") ItemDeApoioForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                categoriaService.salvar(restauranteId, form);
                redirect.addFlashAttribute("mensagem", "Categoria salva com sucesso.");
                return "redirect:/restaurantes/" + restauranteId + "/categorias";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        prepararPagina(restauranteId, model);
        return "apoio/formulario";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long restauranteId, @PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoriaService.excluir(restauranteId, id);
            redirect.addFlashAttribute("mensagem", "Categoria excluída.");
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/restaurantes/" + restauranteId + "/categorias";
    }

    /** Dados que as páginas genéricas de apoio esperam: título, endereço base e link de volta. */
    private void prepararPagina(Long restauranteId, Model model) {
        Restaurante restaurante = restauranteService.buscarPorId(restauranteId);
        model.addAttribute("titulo", "Categorias - " + restaurante.getNome());
        model.addAttribute("urlBase", "/restaurantes/" + restauranteId + "/categorias");
        model.addAttribute("voltarUrl", "/painel");
    }
}
