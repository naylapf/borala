package com.corp.borala.controller;

import com.corp.borala.form.AvaliacaoForm;
import com.corp.borala.model.Avaliacao;
import com.corp.borala.model.Favorita;
import com.corp.borala.model.Restaurante;
import com.corp.borala.security.UsuarioLogado;
import com.corp.borala.service.AvaliacaoService;
import com.corp.borala.service.BuscaUsuarioService;
import com.corp.borala.service.FavoritaService;
import com.corp.borala.service.VitrineService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import java.util.List;

/** Área do cliente logado: favoritos, avaliações e histórico de buscas dele (sempre os do próprio usuário). */
@Controller
@RequestMapping("/meu")
public class MeuController {

    private final FavoritaService favoritaService;
    private final AvaliacaoService avaliacaoService;
    private final BuscaUsuarioService buscaUsuarioService;
    private final VitrineService vitrineService;

    public MeuController(FavoritaService favoritaService,
                         AvaliacaoService avaliacaoService,
                         BuscaUsuarioService buscaUsuarioService,
                         VitrineService vitrineService) {
        this.favoritaService = favoritaService;
        this.avaliacaoService = avaliacaoService;
        this.buscaUsuarioService = buscaUsuarioService;
        this.vitrineService = vitrineService;
    }

    // ---------- favoritos ----------

    @GetMapping("/favoritos")
    public String favoritos(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        List<Restaurante> restaurantes = favoritaService.listar(usuario.getId()).stream()
                .map(Favorita::getRestaurante).toList();
        model.addAttribute("restaurantes", restaurantes);
        model.addAttribute("notas", vitrineService.notas(restaurantes));
        model.addAttribute("capas", vitrineService.capas(restaurantes));
        return "meu/favoritos";
    }

    /** Favorita ou desfavorita. {@code voltar=favoritos} retorna para a lista; o padrão é a página do restaurante. */
    @PostMapping("/favoritos/{restauranteId}/alternar")
    public String alternarFavorito(@PathVariable Long restauranteId,
                                   @RequestParam(required = false) String voltar,
                                   @AuthenticationPrincipal UsuarioLogado usuario,
                                   RedirectAttributes redirect) {
        boolean favoritou = favoritaService.alternar(usuario.getId(), restauranteId);
        redirect.addFlashAttribute("mensagem", favoritou ? "Adicionado aos favoritos." : "Removido dos favoritos.");
        return "favoritos".equals(voltar) ? "redirect:/meu/favoritos" : "redirect:/restaurantes/" + restauranteId + "/ver";
    }

    // ---------- avaliações ----------

    @GetMapping("/avaliacoes")
    public String avaliacoes(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("avaliacoes", avaliacaoService.listar(usuario.getId()));
        return "meu/avaliacoes";
    }

    /** Cria a avaliação do cliente para o restaurante ou, se ele já avaliou, atualiza a existente. */
    @PostMapping("/avaliacoes")
    public String avaliar(@Valid @ModelAttribute("form") AvaliacaoForm form, BindingResult resultado,
                          @AuthenticationPrincipal UsuarioLogado usuario, RedirectAttributes redirect) {
        if (resultado.hasErrors() || form.getRestauranteId() == null) {
            redirect.addFlashAttribute("erro", resultado.hasErrors()
                    ? resultado.getAllErrors().get(0).getDefaultMessage()
                    : "Restaurante não informado.");
            return form.getRestauranteId() == null ? "redirect:/" : "redirect:/restaurantes/" + form.getRestauranteId() + "/ver";
        }
        form.setId(avaliacaoService.buscarDoUsuarioNoRestaurante(usuario.getId(), form.getRestauranteId())
                .map(Avaliacao::getId).orElse(null));
        avaliacaoService.salvar(usuario.getId(), form);
        redirect.addFlashAttribute("mensagem", "Avaliação salva. Obrigado!");
        return "redirect:/restaurantes/" + form.getRestauranteId() + "/ver";
    }

    @PostMapping("/avaliacoes/{id}/excluir")
    public String excluirAvaliacao(@PathVariable Long id, @AuthenticationPrincipal UsuarioLogado usuario,
                                   RedirectAttributes redirect) {
        avaliacaoService.excluir(usuario.getId(), id);
        redirect.addFlashAttribute("mensagem", "Avaliação excluída.");
        return "redirect:/meu/avaliacoes";
    }

    // ---------- histórico de buscas ----------

    @GetMapping("/buscas")
    public String buscas(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("buscas", buscaUsuarioService.listar(usuario.getId()));
        return "meu/buscas";
    }

    @PostMapping("/buscas/{id}/excluir")
    public String excluirBusca(@PathVariable Long id, @AuthenticationPrincipal UsuarioLogado usuario,
                               RedirectAttributes redirect) {
        buscaUsuarioService.excluir(usuario.getId(), id);
        redirect.addFlashAttribute("mensagem", "Busca removida do histórico.");
        return "redirect:/meu/buscas";
    }

    @PostMapping("/buscas/limpar")
    public String limparBuscas(@AuthenticationPrincipal UsuarioLogado usuario, RedirectAttributes redirect) {
        buscaUsuarioService.limpar(usuario.getId());
        redirect.addFlashAttribute("mensagem", "Histórico de buscas apagado.");
        return "redirect:/meu/buscas";
    }
}
