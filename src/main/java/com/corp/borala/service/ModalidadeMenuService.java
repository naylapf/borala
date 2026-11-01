package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.ItemDeApoioForm;
import com.corp.borala.model.ModalidadeMenu;
import com.corp.borala.repository.ModalidadeMenuRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD de modalidades de menu. */
@Service
public class ModalidadeMenuService {

    private final ModalidadeMenuRepository modalidadeMenuRepository;

    public ModalidadeMenuService(ModalidadeMenuRepository modalidadeMenuRepository) {
        this.modalidadeMenuRepository = modalidadeMenuRepository;
    }

    @Transactional(readOnly = true)
    public List<ModalidadeMenu> listar() {
        return modalidadeMenuRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public ItemDeApoioForm montarFormulario(Long id) {
        ModalidadeMenu modalidade = buscar(id);
        ItemDeApoioForm form = new ItemDeApoioForm();
        form.setId(modalidade.getId());
        form.setNome(modalidade.getNome());
        return form;
    }

    @Transactional
    public void salvar(ItemDeApoioForm form) {
        String nome = form.getNome().trim();
        validarNomeUnico(nome, form.getId());

        if (form.getId() == null) {
            modalidadeMenuRepository.save(new ModalidadeMenu(nome));
        } else {
            buscar(form.getId()).atualizar(nome);
        }
    }

    @Transactional
    public void excluir(Long id) {
        ModalidadeMenu modalidade = buscar(id);
        try {
            modalidadeMenuRepository.delete(modalidade);
            modalidadeMenuRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir \"" + modalidade.getNome() + "\": ela está em uso por restaurantes.");
        }
    }

    private ModalidadeMenu buscar(Long id) {
        return modalidadeMenuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modalidade de menu não encontrada"));
    }

    private void validarNomeUnico(String nome, Long idAtual) {
        modalidadeMenuRepository.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("nome", "Já existe uma modalidade com este nome");
                });
    }
}
