package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.PratoForm;
import com.corp.borala.model.Categoria;
import com.corp.borala.model.Prato;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.repository.CategoriaRepository;
import com.corp.borala.repository.ComposicaoRepository;
import com.corp.borala.repository.PratoRepository;
import com.corp.borala.repository.RestauranteRepository;
import com.corp.borala.repository.TipoCulinariaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/** CRUD de pratos do cardápio de um restaurante, com pesquisa por nome. */
@Service
public class PratoService {

    private final PratoRepository pratoRepository;
    private final CategoriaRepository categoriaRepository;
    private final TipoCulinariaRepository tipoCulinariaRepository;
    private final RestauranteRepository restauranteRepository;
    private final ComposicaoRepository composicaoRepository;
    private final RestauranteService restauranteService;
    private final UploadService uploadService;

    public PratoService(PratoRepository pratoRepository,
                        CategoriaRepository categoriaRepository,
                        TipoCulinariaRepository tipoCulinariaRepository,
                        RestauranteRepository restauranteRepository,
                        ComposicaoRepository composicaoRepository,
                        RestauranteService restauranteService,
                        UploadService uploadService) {
        this.uploadService = uploadService;
        this.pratoRepository = pratoRepository;
        this.categoriaRepository = categoriaRepository;
        this.tipoCulinariaRepository = tipoCulinariaRepository;
        this.restauranteRepository = restauranteRepository;
        this.composicaoRepository = composicaoRepository;
        this.restauranteService = restauranteService;
    }

    /** Lista os pratos do restaurante. O texto de busca é opcional e filtra pelo nome. */
    @Transactional(readOnly = true)
    public List<Prato> listar(Long restauranteId, String busca) {
        String termo = busca == null ? "" : busca.trim();
        return pratoRepository.buscarPorRestaurante(restauranteId, termo);
    }

    @Transactional(readOnly = true)
    public PratoForm montarFormulario(Long restauranteId, Long pratoId) {
        Prato prato = buscarDoRestaurante(restauranteId, pratoId);
        PratoForm form = new PratoForm();
        form.setId(prato.getId());
        form.setNome(prato.getNome());
        form.setDescricao(prato.getDescricao());
        form.setPreco(prato.getPreco());
        form.setDisponibilidade(prato.isDisponibilidade());
        form.setRendimento(prato.getRendimento());
        form.setFoto(prato.getFoto());
        form.setCategoriaId(prato.getCategoria().getId());
        form.setTipoCulinariaId(prato.getTipoCulinaria().getId());
        return form;
    }

    @Transactional
    public void salvar(Long restauranteId, PratoForm form) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
        // a categoria precisa ser do cardápio deste restaurante
        Categoria categoria = categoriaRepository.findByIdAndCardapioRestauranteId(form.getCategoriaId(), restauranteId)
                .orElseThrow(() -> new CampoInvalidoException("categoriaId", "Selecione uma categoria deste restaurante"));
        TipoCulinaria tipo = tipoCulinariaRepository.findById(form.getTipoCulinariaId())
                .orElseThrow(() -> new CampoInvalidoException("tipoCulinariaId", "Tipo de culinária inválido"));

        String nome = form.getNome().trim();
        String descricao = form.getDescricao().trim();
        MultipartFile arquivo = form.getArquivoFoto();
        boolean temArquivo = arquivo != null && !arquivo.isEmpty();

        if (form.getId() == null) {
            String foto = temArquivo ? uploadService.salvarImagem(arquivo, "arquivoFoto") : null;
            pratoRepository.save(new Prato(nome, descricao, form.getPreco(), form.isDisponibilidade(),
                    form.getRendimento(), foto, categoria, tipo));
        } else {
            Prato prato = buscarDoRestaurante(restauranteId, form.getId());
            // a foto atual vem do banco, nunca do formulário
            String fotoAntiga = prato.getFoto();
            String foto = fotoAntiga;
            if (temArquivo) {
                foto = uploadService.salvarImagem(arquivo, "arquivoFoto");
            } else if (form.isRemoverFoto()) {
                foto = null;
            }
            prato.atualizar(nome, descricao, form.getPreco(), form.isDisponibilidade(),
                    form.getRendimento(), foto, categoria, tipo);
            if (!Objects.equals(fotoAntiga, foto)) {
                uploadService.excluir(fotoAntiga);
            }
        }

        // regra combinada: os tipos de culinária do restaurante acompanham os pratos cadastrados
        restaurante.adicionarTipoCulinaria(tipo);
        categoria.getCardapio().registrarAtualizacao();

        // a faixa de preço do restaurante acompanha os pratos: menor e maior preço
        pratoRepository.flush();
        restauranteService.recalcularFaixaPreco(restauranteId);
    }

    @Transactional
    public void excluir(Long restauranteId, Long pratoId) {
        Prato prato = buscarDoRestaurante(restauranteId, pratoId);
        prato.getCategoria().getCardapio().registrarAtualizacao();
        // o prato sai de todas as ofertas de menu em que estava
        composicaoRepository.deleteByPratoId(pratoId);
        pratoRepository.delete(prato);

        pratoRepository.flush();
        uploadService.excluir(prato.getFoto());
        restauranteService.recalcularFaixaPreco(restauranteId);
    }

    private Prato buscarDoRestaurante(Long restauranteId, Long pratoId) {
        return pratoRepository.buscarPorIdDoRestaurante(pratoId, restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));
    }
}
