package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.AvaliacaoForm;
import com.corp.borala.model.Avaliacao;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.AvaliacaoRepository;
import com.corp.borala.repository.RestauranteRepository;
import com.corp.borala.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** CRUD das avaliações que um usuário faz de restaurantes (no máximo uma por restaurante). */
@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            UsuarioRepository usuarioRepository,
                            RestauranteRepository restauranteRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listar(Long usuarioId) {
        return avaliacaoRepository.findByUsuarioIdOrderByDataDescIdDesc(usuarioId);
    }

    /** A avaliação que este usuário já fez deste restaurante, se houver. */
    @Transactional(readOnly = true)
    public Optional<Avaliacao> buscarDoUsuarioNoRestaurante(Long usuarioId, Long restauranteId) {
        return avaliacaoRepository.findByUsuarioIdAndRestauranteId(usuarioId, restauranteId);
    }

    @Transactional(readOnly = true)
    public AvaliacaoForm montarFormulario(Long usuarioId, Long id) {
        Avaliacao avaliacao = buscar(usuarioId, id);
        AvaliacaoForm form = new AvaliacaoForm();
        form.setId(avaliacao.getId());
        form.setRestauranteId(avaliacao.getRestaurante().getId());
        form.setNota(avaliacao.getNota());
        form.setComentario(avaliacao.getComentario());
        return form;
    }

    @Transactional
    public void salvar(Long usuarioId, AvaliacaoForm form) {
        Restaurante restaurante = restauranteRepository.findById(form.getRestauranteId())
                .orElseThrow(() -> new CampoInvalidoException("restauranteId", "Restaurante não encontrado"));
        validarAvaliacaoUnica(usuarioId, restaurante.getId(), form.getId());
        String comentario = (form.getComentario() == null || form.getComentario().isBlank()) ? null : form.getComentario().trim();

        if (form.getId() == null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
            avaliacaoRepository.save(new Avaliacao(usuario, restaurante, form.getNota(), comentario, LocalDate.now()));
        } else {
            buscar(usuarioId, form.getId()).atualizar(restaurante, form.getNota(), comentario, LocalDate.now());
        }
    }

    @Transactional
    public void excluir(Long usuarioId, Long id) {
        avaliacaoRepository.delete(buscar(usuarioId, id));
    }

    private Avaliacao buscar(Long usuarioId, Long id) {
        return avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"));
    }

    private void validarAvaliacaoUnica(Long usuarioId, Long restauranteId, Long idAtual) {
        avaliacaoRepository.findByUsuarioIdAndRestauranteId(usuarioId, restauranteId)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("restauranteId",
                            "Você já avaliou este restaurante. Edite a avaliação existente.");
                });
    }
}
