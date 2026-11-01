package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.ComposicaoForm;
import com.corp.borala.model.Composicao;
import com.corp.borala.model.OfertaMenu;
import com.corp.borala.model.Prato;
import com.corp.borala.repository.ComposicaoRepository;
import com.corp.borala.repository.OfertaMenuRepository;
import com.corp.borala.repository.PratoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD dos pratos que compõem uma oferta de menu (relacionamento Compõe do DER). */
@Service
public class ComposicaoService {

    private final ComposicaoRepository composicaoRepository;
    private final OfertaMenuRepository ofertaMenuRepository;
    private final PratoRepository pratoRepository;

    public ComposicaoService(ComposicaoRepository composicaoRepository,
                             OfertaMenuRepository ofertaMenuRepository,
                             PratoRepository pratoRepository) {
        this.composicaoRepository = composicaoRepository;
        this.ofertaMenuRepository = ofertaMenuRepository;
        this.pratoRepository = pratoRepository;
    }

    @Transactional(readOnly = true)
    public List<Composicao> listar(Long restauranteId, Long ofertaId) {
        buscarOferta(restauranteId, ofertaId);
        return composicaoRepository.findByOfertaMenuIdOrderByPratoNomeAsc(ofertaId);
    }

    @Transactional(readOnly = true)
    public ComposicaoForm montarFormulario(Long restauranteId, Long ofertaId, Long id) {
        Composicao composicao = buscar(restauranteId, ofertaId, id);
        ComposicaoForm form = new ComposicaoForm();
        form.setId(composicao.getId());
        form.setPratoId(composicao.getPrato().getId());
        form.setCobranca(composicao.getCobranca());
        form.setFormaServico(composicao.getFormaServico());
        return form;
    }

    @Transactional
    public void salvar(Long restauranteId, Long ofertaId, ComposicaoForm form) {
        OfertaMenu oferta = buscarOferta(restauranteId, ofertaId);
        // o prato precisa ser do mesmo restaurante da oferta
        Prato prato = pratoRepository.buscarPorIdDoRestaurante(form.getPratoId(), restauranteId)
                .orElseThrow(() -> new CampoInvalidoException("pratoId", "Selecione um prato deste restaurante"));
        validarPratoUnico(ofertaId, prato.getId(), form.getId());
        String cobranca = form.getCobranca().trim();

        if (form.getId() == null) {
            composicaoRepository.save(new Composicao(oferta, prato, cobranca, form.getFormaServico()));
        } else {
            buscar(restauranteId, ofertaId, form.getId()).atualizar(prato, cobranca, form.getFormaServico());
        }
    }

    @Transactional
    public void excluir(Long restauranteId, Long ofertaId, Long id) {
        composicaoRepository.delete(buscar(restauranteId, ofertaId, id));
    }

    private OfertaMenu buscarOferta(Long restauranteId, Long ofertaId) {
        return ofertaMenuRepository.findByIdAndRestauranteId(ofertaId, restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta de menu não encontrada"));
    }

    private Composicao buscar(Long restauranteId, Long ofertaId, Long id) {
        buscarOferta(restauranteId, ofertaId);
        return composicaoRepository.findByIdAndOfertaMenuId(id, ofertaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item da oferta não encontrado"));
    }

    private void validarPratoUnico(Long ofertaId, Long pratoId, Long idAtual) {
        composicaoRepository.findByOfertaMenuIdAndPratoId(ofertaId, pratoId)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("pratoId", "Este prato já faz parte da oferta");
                });
    }
}
