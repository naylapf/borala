package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.ItemDeApoioForm;
import com.corp.borala.model.Cardapio;
import com.corp.borala.model.Categoria;
import com.corp.borala.repository.CardapioRepository;
import com.corp.borala.repository.CategoriaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD das categorias do cardápio de um restaurante (ex.: Massas, Sobremesas). */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CardapioRepository cardapioRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, CardapioRepository cardapioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.cardapioRepository = cardapioRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar(Long restauranteId) {
        return categoriaRepository.findByCardapioRestauranteIdOrderByNomeAsc(restauranteId);
    }

    @Transactional(readOnly = true)
    public ItemDeApoioForm montarFormulario(Long restauranteId, Long id) {
        Categoria categoria = buscar(restauranteId, id);
        ItemDeApoioForm form = new ItemDeApoioForm();
        form.setId(categoria.getId());
        form.setNome(categoria.getNome());
        return form;
    }

    @Transactional
    public void salvar(Long restauranteId, ItemDeApoioForm form) {
        Cardapio cardapio = buscarCardapio(restauranteId);
        String nome = form.getNome().trim();
        validarNomeUnico(cardapio.getId(), nome, form.getId());

        if (form.getId() == null) {
            categoriaRepository.save(new Categoria(nome, cardapio));
        } else {
            buscar(restauranteId, form.getId()).atualizar(nome);
        }
        cardapio.registrarAtualizacao();
    }

    @Transactional
    public void excluir(Long restauranteId, Long id) {
        Categoria categoria = buscar(restauranteId, id);
        try {
            categoriaRepository.delete(categoria);
            categoriaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir a categoria \"" + categoria.getNome() + "\": ela possui pratos.");
        }
        categoria.getCardapio().registrarAtualizacao();
    }

    private Categoria buscar(Long restauranteId, Long id) {
        return categoriaRepository.findByIdAndCardapioRestauranteId(id, restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    private Cardapio buscarCardapio(Long restauranteId) {
        return cardapioRepository.findByRestauranteId(restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
    }

    private void validarNomeUnico(Long cardapioId, String nome, Long idAtual) {
        categoriaRepository.findByCardapioIdAndNomeIgnoreCase(cardapioId, nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("nome", "Já existe uma categoria com este nome neste cardápio");
                });
    }
}
