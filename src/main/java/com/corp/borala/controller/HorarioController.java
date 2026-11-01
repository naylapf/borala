package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.HorarioForm;
import com.corp.borala.model.DiaSemana;
import com.corp.borala.service.HorarioService;
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

/** Telas do CRUD de horários de funcionamento de um restaurante. */
@Controller
@RequestMapping("/restaurantes/{restauranteId}/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final RestauranteService restauranteService;

    public HorarioController(HorarioService horarioService, RestauranteService restauranteService) {
        this.horarioService = horarioService;
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public String listar(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("horarios", horarioService.listar(restauranteId));
        return "horarios/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("form", new HorarioForm());
        return paginaDoFormulario(restauranteId, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long restauranteId, @PathVariable Long id, Model model) {
        model.addAttribute("form", horarioService.montarFormulario(restauranteId, id));
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping
    public String salvar(@PathVariable Long restauranteId,
                         @Valid @ModelAttribute("form") HorarioForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                horarioService.salvar(restauranteId, form);
                redirect.addFlashAttribute("mensagem", "Horário salvo com sucesso.");
                return "redirect:/restaurantes/" + restauranteId + "/horarios";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long restauranteId, @PathVariable Long id, RedirectAttributes redirect) {
        horarioService.excluir(restauranteId, id);
        redirect.addFlashAttribute("mensagem", "Horário excluído.");
        return "redirect:/restaurantes/" + restauranteId + "/horarios";
    }

    private String paginaDoFormulario(Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("dias", DiaSemana.values());
        return "horarios/formulario";
    }
}
