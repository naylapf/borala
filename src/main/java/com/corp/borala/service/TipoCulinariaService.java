package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.ItemDeApoioForm;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.repository.TipoCulinariaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD de tipos de culinária. */
@Service
public class TipoCulinariaService {

    private final TipoCulinariaRepository tipoCulinariaRepository;

    public TipoCulinariaService(TipoCulinariaRepository tipoCulinariaRepository) {
        this.tipoCulinariaRepository = tipoCulinariaRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoCulinaria> listar() {
        return tipoCulinariaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public ItemDeApoioForm montarFormulario(Long id) {
        TipoCulinaria tipo = buscar(id);
        ItemDeApoioForm form = new ItemDeApoioForm();
        form.setId(tipo.getId());
        form.setNome(tipo.getNome());
        return form;
    }

    @Transactional
    public void salvar(ItemDeApoioForm form) {
        String nome = form.getNome().trim();
        validarNomeUnico(nome, form.getId());

        if (form.getId() == null) {
            tipoCulinariaRepository.save(new TipoCulinaria(nome));
        } else {
            // a entidade está gerenciada pelo JPA: a alteração é gravada ao fim da transação
            buscar(form.getId()).atualizar(nome);
        }
    }

    @Transactional
    public void excluir(Long id) {
        TipoCulinaria tipo = buscar(id);
        try {
            tipoCulinariaRepository.delete(tipo);
            tipoCulinariaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir \"" + tipo.getNome() + "\": ele está em uso por restaurantes ou pratos.");
        }
    }

    private TipoCulinaria buscar(Long id) {
        return tipoCulinariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de culinária não encontrado"));
    }

    /** Impede dois tipos com o mesmo nome (ignorando o próprio registro ao editar). */
    private void validarNomeUnico(String nome, Long idAtual) {
        tipoCulinariaRepository.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("nome", "Já existe um tipo de culinária com este nome");
                });
    }
}
