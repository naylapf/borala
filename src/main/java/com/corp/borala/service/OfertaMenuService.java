package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.OfertaMenuForm;
import com.corp.borala.model.ModalidadeMenu;
import com.corp.borala.model.OfertaMenu;
import com.corp.borala.model.Restaurante;
import com.corp.borala.repository.ComposicaoRepository;
import com.corp.borala.repository.ModalidadeMenuRepository;
import com.corp.borala.repository.OfertaMenuRepository;
import com.corp.borala.repository.RestauranteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD das ofertas de menu: as modalidades de menu que um restaurante oferece, com valor e descrição. */
@Service
public class OfertaMenuService {

    private final OfertaMenuRepository ofertaMenuRepository;
    private final RestauranteRepository restauranteRepository;
    private final ModalidadeMenuRepository modalidadeMenuRepository;
    private final ComposicaoRepository composicaoRepository;

    public OfertaMenuService(OfertaMenuRepository ofertaMenuRepository,
                             RestauranteRepository restauranteRepository,
                             ModalidadeMenuRepository modalidadeMenuRepository,
                             ComposicaoRepository composicaoRepository) {
        this.ofertaMenuRepository = ofertaMenuRepository;
        this.restauranteRepository = restauranteRepository;
        this.modalidadeMenuRepository = modalidadeMenuRepository;
        this.composicaoRepository = composicaoRepository;
    }

    @Transactional(readOnly = true)
    public List<OfertaMenu> listar(Long restauranteId) {
        return ofertaMenuRepository.findByRestauranteIdOrderByModalidadeMenuNomeAsc(restauranteId);
    }

    @Transactional(readOnly = true)
    public OfertaMenu buscar(Long restauranteId, Long id) {
        return ofertaMenuRepository.findByIdAndRestauranteId(id, restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta de menu não encontrada"));
    }

    @Transactional(readOnly = true)
    public OfertaMenuForm montarFormulario(Long restauranteId, Long id) {
        OfertaMenu oferta = buscar(restauranteId, id);
        OfertaMenuForm form = new OfertaMenuForm();
        form.setId(oferta.getId());
        form.setModalidadeMenuId(oferta.getModalidadeMenu().getId());
        form.setValor(oferta.getValor());
        form.setDescricao(oferta.getDescricao());
        return form;
    }

    @Transactional
    public void salvar(Long restauranteId, OfertaMenuForm form) {
        ModalidadeMenu modalidade = modalidadeMenuRepository.findById(form.getModalidadeMenuId())
                .orElseThrow(() -> new CampoInvalidoException("modalidadeMenuId", "Modalidade de menu inválida"));
        validarModalidadeUnica(restauranteId, modalidade.getId(), form.getId());
        String descricao = (form.getDescricao() == null || form.getDescricao().isBlank()) ? null : form.getDescricao().trim();

        if (form.getId() == null) {
            Restaurante restaurante = restauranteRepository.findById(restauranteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
            ofertaMenuRepository.save(new OfertaMenu(restaurante, modalidade, form.getValor(), descricao));
        } else {
            buscar(restauranteId, form.getId()).atualizar(modalidade, form.getValor(), descricao);
        }
    }

    /** Regra do DER: todo restaurante com cardápio precisa manter ao menos uma modalidade de menu. */
    @Transactional
    public void excluir(Long restauranteId, Long id) {
        OfertaMenu oferta = buscar(restauranteId, id);
        if (ofertaMenuRepository.countByRestauranteId(restauranteId) <= 1) {
            throw new RegraDeNegocioException("O restaurante precisa ter ao menos uma modalidade de menu.");
        }
        composicaoRepository.deleteByOfertaMenuId(id);
        ofertaMenuRepository.delete(oferta);
    }

    private void validarModalidadeUnica(Long restauranteId, Long modalidadeId, Long idAtual) {
        ofertaMenuRepository.findByRestauranteIdAndModalidadeMenuId(restauranteId, modalidadeId)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("modalidadeMenuId", "O restaurante já oferece esta modalidade");
                });
    }
}
