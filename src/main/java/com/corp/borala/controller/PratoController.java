package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.PratoForm;
import com.corp.borala.service.CategoriaService;
import com.corp.borala.service.PratoService;
import com.corp.borala.service.RestauranteService;
import com.corp.borala.service.TipoCulinariaService;
import com.corp.borala.service.VitrineService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Telas do CRUD de pratos do cardápio de um restaurante. A mesma tela de formulário serve para criar e editar. */
@Controller
@RequestMapping("/restaurantes/{restauranteId}/pratos")
public class PratoController {

    private final PratoService pratoService;
    private final RestauranteService restauranteService;
    private final CategoriaService categoriaService;
    private final TipoCulinariaService tipoCulinariaService;
    private final VitrineService vitrineService;

    public PratoController(PratoService pratoService,
                           RestauranteService restauranteService,
                           CategoriaService categoriaService,
                           TipoCulinariaService tipoCulinariaService,
                           VitrineService vitrineService) {
        this.vitrineService = vitrineService;
        this.pratoService = pratoService;
        this.restauranteService = restauranteService;
        this.categoriaService = categoriaService;
        this.tipoCulinariaService = tipoCulinariaService;
    }

    @GetMapping
    public String listar(@PathVariable Long restauranteId,
                         @RequestParam(required = false) String busca,
                         Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("pratos", pratoService.listar(restauranteId, busca));
        model.addAttribute("busca", busca);
        model.addAttribute("horarios", vitrineService.horarios(restauranteId));
        return "pratos/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long restauranteId, Model model) {
        model.addAttribute("form", new PratoForm());
        return paginaDoFormulario(restauranteId, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long restauranteId, @PathVariable Long id, Model model) {
        model.addAttribute("form", pratoService.montarFormulario(restauranteId, id));
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping
    public String salvar(@PathVariable Long restauranteId,
                         @Valid @ModelAttribute("form") PratoForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                pratoService.salvar(restauranteId, form);
                redirect.addFlashAttribute("mensagem", "Prato salvo com sucesso.");
                return "redirect:/restaurantes/" + restauranteId + "/pratos";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(restauranteId, model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long restauranteId, @PathVariable Long id, RedirectAttributes redirect) {
        pratoService.excluir(restauranteId, id);
        redirect.addFlashAttribute("mensagem", "Prato excluído.");
        return "redirect:/restaurantes/" + restauranteId + "/pratos";
    }

    private String paginaDoFormulario(Long restauranteId, Model model) {
        model.addAttribute("restaurante", restauranteService.buscarPorId(restauranteId));
        model.addAttribute("categorias", categoriaService.listar(restauranteId));
        model.addAttribute("tiposCulinaria", tipoCulinariaService.listar());
        return "pratos/formulario";
    }
}
