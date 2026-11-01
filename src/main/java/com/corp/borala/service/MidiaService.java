package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.MidiaForm;
import com.corp.borala.model.Midia;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoMidia;
import com.corp.borala.repository.MidiaRepository;
import com.corp.borala.repository.RestauranteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD das mídias (fotos e vídeos) exibidas por um restaurante. */
@Service
public class MidiaService {

    private final MidiaRepository midiaRepository;
    private final RestauranteRepository restauranteRepository;

    private final UploadService uploadService;

    public MidiaService(MidiaRepository midiaRepository, RestauranteRepository restauranteRepository,
                        UploadService uploadService) {
        this.midiaRepository = midiaRepository;
        this.restauranteRepository = restauranteRepository;
        this.uploadService = uploadService;
    }

    @Transactional(readOnly = true)
    public List<Midia> listar(Long restauranteId) {
        return midiaRepository.findByRestauranteIdOrderByIdAsc(restauranteId);
    }

    @Transactional(readOnly = true)
    public MidiaForm montarFormulario(Long restauranteId, Long id) {
        Midia midia = buscar(restauranteId, id);
        MidiaForm form = new MidiaForm();
        form.setId(midia.getId());
        form.setTipo(midia.getTipo());
        if (midia.getTipo() == TipoMidia.FOTO) {
            form.setFotoAtual(midia.getUrl());
        } else {
            form.setUrl(midia.getUrl());
        }
        return form;
    }

    /**
     * Foto: usa o arquivo enviado (na edição, sem arquivo novo, mantém a foto atual).
     * Vídeo: usa o link informado. Arquivos que deixam de ser usados são apagados do disco.
     */
    @Transactional
    public void salvar(Long restauranteId, MidiaForm form) {
        Midia existente = form.getId() == null ? null : buscar(restauranteId, form.getId());
        String urlAntiga = existente == null ? null : existente.getUrl();

        String url;
        if (form.getTipo() == TipoMidia.FOTO) {
            MultipartFile arquivo = form.getArquivo();
            if (arquivo != null && !arquivo.isEmpty()) {
                url = uploadService.salvarImagem(arquivo, "arquivo");
            } else if (existente != null && existente.getTipo() == TipoMidia.FOTO) {
                url = urlAntiga;
            } else {
                throw new CampoInvalidoException("arquivo", "Escolha uma imagem (JPG, PNG ou WEBP)");
            }
        } else {
            if (form.getUrl() == null || form.getUrl().isBlank()) {
                throw new CampoInvalidoException("url", "Informe o link do vídeo");
            }
            url = form.getUrl().trim();
        }
        validarUrlUnica(restauranteId, url, form.getId());

        if (existente == null) {
            Restaurante restaurante = restauranteRepository.findById(restauranteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
            midiaRepository.save(new Midia(restaurante, url, form.getTipo()));
        } else {
            existente.atualizar(url, form.getTipo());
            if (!urlAntiga.equals(url)) {
                uploadService.excluir(urlAntiga);
            }
        }
    }

    @Transactional
    public void excluir(Long restauranteId, Long id) {
        Midia midia = buscar(restauranteId, id);
        midiaRepository.delete(midia);
        uploadService.excluir(midia.getUrl());
    }

    private Midia buscar(Long restauranteId, Long id) {
        return midiaRepository.findByIdAndRestauranteId(id, restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mídia não encontrada"));
    }

    private void validarUrlUnica(Long restauranteId, String url, Long idAtual) {
        midiaRepository.findByRestauranteIdAndUrl(restauranteId, url)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("url", "Esta mídia já foi cadastrada neste restaurante");
                });
    }
}
