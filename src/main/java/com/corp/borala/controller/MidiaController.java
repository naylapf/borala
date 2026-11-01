package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.MidiaForm;
import com.corp.borala.model.TipoMidia;
import com.corp.borala.service.MidiaService;
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

/** Telas do CRUD de mídias (fotos e vídeos) de um restaurante. */
@Controller
@RequestMapping("/restaurantes/{restauranteId}/midias")
public class MidiaController {

    private final MidiaService midiaService;
    private final RestauranteService restauranteService;

    public MidiaController(MidiaService midiaService, RestauranteService restauranteService) {
        this.midiaService = midiaService;
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public String listar(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("midias", midiaService.listar(restauranteId));
        return "midias/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("form", new MidiaForm());
        return paginaDoFormulario(restauranteId, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long restauranteId, @PathVariable Long id, Model model) {
        model.addAttribute("form", midiaService.montarFormulario(restauranteId, id));
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping
    public String salvar(@PathVariable Long restauranteId,
                         @Valid @ModelAttribute("form") MidiaForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                midiaService.salvar(restauranteId, form);
                redirect.addFlashAttribute("mensagem", "Mídia salva com sucesso.");
                return "redirect:/restaurantes/" + restauranteId + "/midias";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long restauranteId, @PathVariable Long id, RedirectAttributes redirect) {
        midiaService.excluir(restauranteId, id);
        redirect.addFlashAttribute("mensagem", "Mídia excluída.");
        return "redirect:/restaurantes/" + restauranteId + "/midias";
    }

    private String paginaDoFormulario(Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("tipos", TipoMidia.values());
        return "midias/formulario";
    }
}
