package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.FavoritaForm;
import com.corp.borala.model.Favorita;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.FavoritaRepository;
import com.corp.borala.repository.RestauranteRepository;
import com.corp.borala.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/** Restaurantes favoritos de um usuário: listar, favoritar e remover. */
@Service
public class FavoritaService {

    private final FavoritaRepository favoritaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;

    public FavoritaService(FavoritaRepository favoritaRepository,
                           UsuarioRepository usuarioRepository,
                           RestauranteRepository restauranteRepository) {
        this.favoritaRepository = favoritaRepository;
        this.usuarioRepository = usuarioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional(readOnly = true)
    public List<Favorita> listar(Long usuarioId) {
        return favoritaRepository.findByUsuarioIdOrderByRestauranteNomeAsc(usuarioId);
    }

    @Transactional
    public void adicionar(Long usuarioId, FavoritaForm form) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Restaurante restaurante = restauranteRepository.findById(form.getRestauranteId())
                .orElseThrow(() -> new CampoInvalidoException("restauranteId", "Restaurante não encontrado"));

        if (favoritaRepository.findByUsuarioIdAndRestauranteId(usuarioId, restaurante.getId()).isPresent()) {
            throw new CampoInvalidoException("restauranteId", "Este restaurante já está nos favoritos");
        }
        favoritaRepository.save(new Favorita(usuario, restaurante, LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public boolean ehFavorito(Long usuarioId, Long restauranteId) {
        return favoritaRepository.findByUsuarioIdAndRestauranteId(usuarioId, restauranteId).isPresent();
    }

    /** Favorita se ainda não é favorito; senão remove. Devolve true quando o restaurante ficou favoritado. */
    @Transactional
    public boolean alternar(Long usuarioId, Long restauranteId) {
        var existente = favoritaRepository.findByUsuarioIdAndRestauranteId(usuarioId, restauranteId);
        if (existente.isPresent()) {
            favoritaRepository.delete(existente.get());
            return false;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
        favoritaRepository.save(new Favorita(usuario, restaurante, LocalDate.now()));
        return true;
    }

    @Transactional
    public void excluir(Long usuarioId, Long id) {
        Favorita favorita = favoritaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorito não encontrado"));
        favoritaRepository.delete(favorita);
    }
}
