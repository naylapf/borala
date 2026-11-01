package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.ComposicaoForm;
import com.corp.borala.model.FormaServico;
import com.corp.borala.service.ComposicaoService;
import com.corp.borala.service.OfertaMenuService;
import com.corp.borala.service.PratoService;
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

/** Telas dos pratos que compõem uma oferta de menu (relacionamento Compõe). */
@Controller
@RequestMapping("/restaurantes/{restauranteId}/ofertas/{ofertaId}/pratos")
public class ComposicaoController {

    private final ComposicaoService composicaoService;
    private final OfertaMenuService ofertaMenuService;
    private final RestauranteService restauranteService;
    private final PratoService pratoService;

    public ComposicaoController(ComposicaoService composicaoService,
                                OfertaMenuService ofertaMenuService,
                                RestauranteService restauranteService,
                                PratoService pratoService) {
        this.composicaoService = composicaoService;
        this.ofertaMenuService = ofertaMenuService;
        this.restauranteService = restauranteService;
        this.pratoService = pratoService;
    }

    @GetMapping
    public String listar(@PathVariable Long restauranteId, @PathVariable Long ofertaId, Model model) {
        adicionarContexto(restauranteId, ofertaId, model);
        model.addAttribute("composicoes", composicaoService.listar(restauranteId, ofertaId));
        return "composicoes/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long restauranteId, @PathVariable Long ofertaId, Model model) {
        model.addAttribute("form", new ComposicaoForm());
        return paginaDoFormulario(restauranteId, ofertaId, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long restauranteId, @PathVariable Long ofertaId,
                         @PathVariable Long id, Model model) {
        model.addAttribute("form", composicaoService.montarFormulario(restauranteId, ofertaId, id));
        return paginaDoFormulario(restauranteId, ofertaId, model);
    }

    @PostMapping
    public String salvar(@PathVariable Long restauranteId, @PathVariable Long ofertaId,
                         @Valid @ModelAttribute("form") ComposicaoForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                composicaoService.salvar(restauranteId, ofertaId, form);
                redirect.addFlashAttribute("mensagem", "Prato salvo na oferta com sucesso.");
                return "redirect:" + urlDaLista(restauranteId, ofertaId);
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(restauranteId, ofertaId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long restauranteId, @PathVariable Long ofertaId,
                          @PathVariable Long id, RedirectAttributes redirect) {
        composicaoService.excluir(restauranteId, ofertaId, id);
        redirect.addFlashAttribute("mensagem", "Prato removido da oferta.");
        return "redirect:" + urlDaLista(restauranteId, ofertaId);
    }

    private String paginaDoFormulario(Long restauranteId, Long ofertaId, Model model) {
        adicionarContexto(restauranteId, ofertaId, model);
        model.addAttribute("pratos", pratoService.listar(restauranteId, ""));
        model.addAttribute("formas", FormaServico.values());
        return "composicoes/formulario";
    }

    /** Restaurante e oferta aparecem no título e nos links de volta das páginas. */
    private void adicionarContexto(Long restauranteId, Long ofertaId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("oferta", ofertaMenuService.buscar(restauranteId, ofertaId));
    }

    private String urlDaLista(Long restauranteId, Long ofertaId) {
        return "/restaurantes/" + restauranteId + "/ofertas/" + ofertaId + "/pratos";
    }
}
