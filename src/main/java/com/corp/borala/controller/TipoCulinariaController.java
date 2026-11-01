package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.ItemDeApoioForm;
import com.corp.borala.service.TipoCulinariaService;
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

/** Telas do CRUD de tipos de culinária. Usa as páginas genéricas da pasta templates/apoio. */
@Controller
@RequestMapping("/tipos-culinaria")
public class TipoCulinariaController {

    private static final String URL_BASE = "/tipos-culinaria";
    private static final String TITULO = "Tipos de culinária";

    private final TipoCulinariaService tipoCulinariaService;

    public TipoCulinariaController(TipoCulinariaService tipoCulinariaService) {
        this.tipoCulinariaService = tipoCulinariaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("titulo", TITULO);
        model.addAttribute("urlBase", URL_BASE);
        model.addAttribute("itens", tipoCulinariaService.listar());
        return "apoio/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new ItemDeApoioForm());
        return paginaDoFormulario(model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", tipoCulinariaService.montarFormulario(id));
        return paginaDoFormulario(model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") ItemDeApoioForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                tipoCulinariaService.salvar(form);
                redirect.addFlashAttribute("mensagem", "Tipo de culinária salvo com sucesso.");
                return "redirect:" + URL_BASE;
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(model);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            tipoCulinariaService.excluir(id);
            redirect.addFlashAttribute("mensagem", "Tipo de culinária excluído.");
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:" + URL_BASE;
    }

    private String paginaDoFormulario(Model model) {
        model.addAttribute("titulo", TITULO);
        model.addAttribute("urlBase", URL_BASE);
        return "apoio/formulario";
    }
}
