package com.corp.borala.controller;

import com.corp.borala.form.RestauranteFiltro;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.security.UsuarioLogado;
import com.corp.borala.service.BuscaUsuarioService;
import com.corp.borala.service.Recomendacao;
import com.corp.borala.service.RestauranteService;
import com.corp.borala.service.TipoCulinariaService;
import com.corp.borala.service.VitrineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Página inicial: busca de restaurantes por nome, endereço, culinária e faixa de preço.
 * Sem busca ativa, mostra também os destaques (mais bem avaliados) e as recomendações do cliente logado.
 */
@Controller
public class HomeController {

    private final RestauranteService restauranteService;
    private final TipoCulinariaService tipoCulinariaService;
    private final BuscaUsuarioService buscaUsuarioService;
    private final VitrineService vitrineService;

    public HomeController(RestauranteService restauranteService,
                          TipoCulinariaService tipoCulinariaService,
                          BuscaUsuarioService buscaUsuarioService,
                          VitrineService vitrineService) {
        this.restauranteService = restauranteService;
        this.tipoCulinariaService = tipoCulinariaService;
        this.buscaUsuarioService = buscaUsuarioService;
        this.vitrineService = vitrineService;
    }

    @GetMapping("/")
    public String inicio(@ModelAttribute("filtro") RestauranteFiltro filtro, BindingResult ignorado,
                         @AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        List<TipoCulinaria> tipos = tipoCulinariaService.listar();
        List<Restaurante> resultado = restauranteService.buscar(filtro);
        List<Restaurante> exibidos = new ArrayList<>(resultado);

        model.addAttribute("tiposCulinaria", tipos);
        model.addAttribute("restaurantes", resultado);

        // destaques e recomendações só aparecem quando o usuário ainda não está filtrando
        if (filtro.isVazio()) {
            List<Restaurante> destaques = vitrineService.destaques();
            model.addAttribute("destaques", destaques);
            exibidos.addAll(destaques);
            if (usuario != null && usuario.isCliente()) {
                Recomendacao recomendacao = vitrineService.recomendados(usuario.getId());
                model.addAttribute("recomendacao", recomendacao);
                exibidos.addAll(recomendacao.getRestaurantes());
            }
            Map<TipoCulinaria, List<Restaurante>> secoes = vitrineService.porCulinaria(tipos);
            model.addAttribute("secoes", secoes);
            secoes.values().forEach(exibidos::addAll);
        }
        model.addAttribute("notas", vitrineService.notas(exibidos));
        model.addAttribute("capas", vitrineService.capas(exibidos));

        // a busca do cliente é registrada sozinha no histórico dele
        if (usuario != null && usuario.isCliente() && !filtro.isVazio()) {
            String culinaria = tipos.stream()
                    .filter(tipo -> tipo.getId().equals(filtro.getTipoCulinariaId()))
                    .map(TipoCulinaria::getNome)
                    .findFirst()
                    .orElse(null);
            buscaUsuarioService.registrarBusca(usuario.getId(), filtro, culinaria);
        }
        return "index";
    }
}
