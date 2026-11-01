package com.corp.borala.controller;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.RestauranteFiltro;
import com.corp.borala.form.RestauranteForm;
import com.corp.borala.security.UsuarioLogado;
import com.corp.borala.service.RestauranteService;
import com.corp.borala.service.TipoCulinariaService;
import com.corp.borala.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Telas do CRUD de restaurantes. A lista, o cadastro e a exclusão são do ADMIN;
 * a edição também é permitida ao dono do restaurante (que não troca dono nem verificação).
 */
@Controller
@RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final TipoCulinariaService tipoCulinariaService;
    private final UsuarioService usuarioService;

    public RestauranteController(RestauranteService restauranteService,
                                 TipoCulinariaService tipoCulinariaService,
                                 UsuarioService usuarioService) {
        this.restauranteService = restauranteService;
        this.tipoCulinariaService = tipoCulinariaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@ModelAttribute("filtro") RestauranteFiltro filtro, BindingResult ignorado, Model model) {
        model.addAttribute("restaurantes", restauranteService.buscar(filtro));
        model.addAttribute("tiposCulinaria", tipoCulinariaService.listar());
        return "restaurantes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new RestauranteForm());
        return paginaDoFormulario(model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", restauranteService.montarFormulario(id));
        return paginaDoFormulario(model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") RestauranteForm form, BindingResult resultado,
                         @AuthenticationPrincipal UsuarioLogado usuario,
                         Model model, RedirectAttributes redirect) {
        exigirPermissao(form, usuario);
        if (!resultado.hasErrors()) {
            try {
                restauranteService.salvar(form, usuario.isAdmin());
                redirect.addFlashAttribute("mensagem", "Restaurante salvo com sucesso.");
                return "redirect:/painel";
            } catch (CampoInvalidoException e) {
                resultado.rejectValue(e.getCampo(), "campo.invalido", e.getMessage());
            }
        }
        return paginaDoFormulario(model);
    }

    /** Botão "Verificar" da listagem do ADMIN. */
    @PostMapping("/{id}/verificar")
    public String verificar(@PathVariable Long id, RedirectAttributes redirect) {
        boolean verificado = restauranteService.alternarVerificacao(id);
        redirect.addFlashAttribute("mensagem", verificado ? "Restaurante verificado." : "Verificação removida.");
        return "redirect:/restaurantes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            restauranteService.excluir(id);
            redirect.addFlashAttribute("mensagem", "Restaurante excluído.");
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/restaurantes";
    }

    /** Criar restaurante pelo CRUD é do ADMIN; editar é do ADMIN ou do dono daquele restaurante. */
    private void exigirPermissao(RestauranteForm form, UsuarioLogado usuario) {
        if (usuario.isAdmin()) {
            return;
        }
        boolean donoDoRestaurante = form.getId() != null
                && usuario.isDono()
                && restauranteService.pertenceAoDono(form.getId(), usuario.getId());
        if (!donoDoRestaurante) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private String paginaDoFormulario(Model model) {
        model.addAttribute("acao", "/restaurantes");
        model.addAttribute("donos", usuarioService.listarDonos());
        model.addAttribute("tiposCulinaria", tipoCulinariaService.listar());
        return "restaurantes/formulario";
    }
}
