package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.OfertaMenuForm;
import com.corp.borala.service.ModalidadeMenuService;
import com.corp.borala.service.OfertaMenuService;
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

/** Telas do CRUD de ofertas de menu (modalidades que o restaurante oferece, com valor e descrição). */
@Controller
@RequestMapping("/restaurantes/{restauranteId}/ofertas")
public class OfertaMenuController {

    private final OfertaMenuService ofertaMenuService;
    private final RestauranteService restauranteService;
    private final ModalidadeMenuService modalidadeMenuService;

    public OfertaMenuController(OfertaMenuService ofertaMenuService,
                                RestauranteService restauranteService,
                                ModalidadeMenuService modalidadeMenuService) {
        this.ofertaMenuService = ofertaMenuService;
        this.restauranteService = restauranteService;
        this.modalidadeMenuService = modalidadeMenuService;
    }

    @GetMapping
    public String listar(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("ofertas", ofertaMenuService.listar(restauranteId));
        return "ofertas/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("form", new OfertaMenuForm());
        return paginaDoFormulario(restauranteId, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long restauranteId, @PathVariable Long id, Model model) {
        model.addAttribute("form", ofertaMenuService.montarFormulario(restauranteId, id));
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping
    public String salvar(@PathVariable Long restauranteId,
                         @Valid @ModelAttribute("form") OfertaMenuForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                ofertaMenuService.salvar(restauranteId, form);
                redirect.addFlashAttribute("mensagem", "Oferta de menu salva com sucesso.");
                return "redirect:/restaurantes/" + restauranteId + "/ofertas";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long restauranteId, @PathVariable Long id, RedirectAttributes redirect) {
        try {
            ofertaMenuService.excluir(restauranteId, id);
            redirect.addFlashAttribute("mensagem", "Oferta de menu excluída.");
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/restaurantes/" + restauranteId + "/ofertas";
    }

    private String paginaDoFormulario(Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("modalidades", modalidadeMenuService.listar());
        return "ofertas/formulario";
    }
}
