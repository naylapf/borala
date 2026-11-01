package com.corp.borala.security;

import com.corp.borala.service.RestauranteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Protege tudo que fica dentro de um restaurante (/restaurantes/{id}/pratos, /horarios, /midias...):
 * o ADMIN acessa qualquer um; o DONO só os restaurantes que ele gerencia.
 */
@Component
public class AcessoRestauranteInterceptor implements HandlerInterceptor {

    private static final Pattern URL_DO_RESTAURANTE = Pattern.compile("^/restaurantes/(\\d+)(/.*)?$");

    private final RestauranteService restauranteService;

    public AcessoRestauranteInterceptor(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        Matcher url = URL_DO_RESTAURANTE.matcher(caminho);
        if (!url.matches()) {
            return true;
        }
        Long restauranteId = Long.valueOf(url.group(1));

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao != null && autenticacao.getPrincipal() instanceof UsuarioLogado usuario) {
            if (usuario.isAdmin()) {
                return true;
            }
            if (usuario.isDono() && restauranteService.pertenceAoDono(restauranteId, usuario.getId())) {
                return true;
            }
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
