package com.corp.borala.controller;

import com.corp.borala.model.Midia;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoMidia;
import com.corp.borala.security.UsuarioLogado;
import com.corp.borala.service.AvaliacaoService;
import com.corp.borala.service.FavoritaService;
import com.corp.borala.service.RestauranteService;
import com.corp.borala.service.VitrineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/** Página pública de um restaurante (acessível sem login): fotos, horários, ofertas, cardápio e avaliações. */
@Controller
public class RestaurantePublicoController {

    private final RestauranteService restauranteService;
    private final VitrineService vitrineService;
    private final FavoritaService favoritaService;
    private final AvaliacaoService avaliacaoService;

    public RestaurantePublicoController(RestauranteService restauranteService,
                                        VitrineService vitrineService,
                                        FavoritaService favoritaService,
                                        AvaliacaoService avaliacaoService) {
        this.restauranteService = restauranteService;
        this.vitrineService = vitrineService;
        this.favoritaService = favoritaService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/restaurantes/{id}/ver")
    public String ver(@PathVariable Long id, @AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        Restaurante restaurante = restauranteService.buscarPorId(id);
        List<Midia> midias = vitrineService.midias(id);

        model.addAttribute("restaurante", restaurante);
        model.addAttribute("resumo", vitrineService.resumo(restaurante));
        model.addAttribute("fotos", midias.stream().filter(m -> m.getTipo() == TipoMidia.FOTO).toList());
        model.addAttribute("videos", midias.stream().filter(m -> m.getTipo() == TipoMidia.VIDEO).toList());
        model.addAttribute("horarios", vitrineService.horarios(id));
        model.addAttribute("ofertas", vitrineService.ofertas(id));
        model.addAttribute("cardapio", vitrineService.cardapio(id));
        model.addAttribute("avaliacoes", vitrineService.avaliacoes(id));

        if (usuario != null && usuario.isCliente()) {
            model.addAttribute("favorito", favoritaService.ehFavorito(usuario.getId(), id));
            model.addAttribute("minhaAvaliacao",
                    avaliacaoService.buscarDoUsuarioNoRestaurante(usuario.getId(), id).orElse(null));
        }
        return "restaurantes/ver";
    }
}
