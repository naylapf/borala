package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.RestauranteFiltro;
import com.corp.borala.form.RestauranteForm;
import com.corp.borala.model.Cardapio;
import com.corp.borala.model.Endereco;
import com.corp.borala.model.Papel;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.CardapioRepository;
import com.corp.borala.repository.AvaliacaoRepository;
import com.corp.borala.repository.CategoriaRepository;
import com.corp.borala.repository.ComposicaoRepository;
import com.corp.borala.repository.FavoritaRepository;
import com.corp.borala.repository.HorarioRepository;
import com.corp.borala.repository.MidiaRepository;
import com.corp.borala.repository.OfertaMenuRepository;
import com.corp.borala.repository.PratoRepository;
import com.corp.borala.repository.RestauranteRepository;
import com.corp.borala.repository.TipoCulinariaRepository;
import com.corp.borala.repository.RestauranteSpecs;
import com.corp.borala.repository.UsuarioRepository;
import com.corp.borala.util.Normalizador;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** CRUD de restaurantes, busca com filtros e cálculo automático da faixa de preço. */
@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final CardapioRepository cardapioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PratoRepository pratoRepository;
    private final HorarioRepository horarioRepository;
    private final MidiaRepository midiaRepository;
    private final TipoCulinariaRepository tipoCulinariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaMenuRepository ofertaMenuRepository;
    private final ComposicaoRepository composicaoRepository;
    private final FavoritaRepository favoritaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final UploadService uploadService;

    public RestauranteService(RestauranteRepository restauranteRepository,
                              CardapioRepository cardapioRepository,
                              CategoriaRepository categoriaRepository,
                              PratoRepository pratoRepository,
                              HorarioRepository horarioRepository,
                              MidiaRepository midiaRepository,
                              TipoCulinariaRepository tipoCulinariaRepository,
                              UsuarioRepository usuarioRepository,
                              OfertaMenuRepository ofertaMenuRepository,
                              ComposicaoRepository composicaoRepository,
                              FavoritaRepository favoritaRepository,
                              AvaliacaoRepository avaliacaoRepository,
                              UploadService uploadService) {
        this.uploadService = uploadService;
        this.restauranteRepository = restauranteRepository;
        this.cardapioRepository = cardapioRepository;
        this.categoriaRepository = categoriaRepository;
        this.pratoRepository = pratoRepository;
        this.horarioRepository = horarioRepository;
        this.midiaRepository = midiaRepository;
        this.tipoCulinariaRepository = tipoCulinariaRepository;
        this.usuarioRepository = usuarioRepository;
        this.ofertaMenuRepository = ofertaMenuRepository;
        this.composicaoRepository = composicaoRepository;
        this.favoritaRepository = favoritaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    /**
     * Lista os restaurantes aplicando só os filtros preenchidos (nome, cidade, bairro, culinária e faixa de preço).
     * A busca por texto é parcial e não diferencia maiúsculas.
     */
    @Transactional(readOnly = true)
    public List<Restaurante> buscar(RestauranteFiltro filtro) {
        RestauranteFiltro f = ajustarIntervaloDePreco(filtro);
        return restauranteRepository.findAll(RestauranteSpecs.comFiltro(f), Sort.by("nome"));
    }

    /** Todos os restaurantes, sem filtro (usado nos seletores das telas de administração). */
    @Transactional(readOnly = true)
    public List<Restaurante> listarTodos() {
        return buscar(new RestauranteFiltro());
    }

    /** Restaurantes gerenciados por um dono. */
    @Transactional(readOnly = true)
    public List<Restaurante> listarDoDono(Long donoId) {
        return restauranteRepository.findByDonoIdOrderByNomeAsc(donoId);
    }

    /** O restaurante pertence a este usuário? */
    @Transactional(readOnly = true)
    public boolean pertenceAoDono(Long restauranteId, Long donoId) {
        return restauranteRepository.existsByIdAndDonoId(restauranteId, donoId);
    }

    /** Se o usuário digitou "de" maior que "até", inverte os dois em vez de devolver uma busca vazia. */
    private RestauranteFiltro ajustarIntervaloDePreco(RestauranteFiltro filtro) {
        if (filtro.getPrecoDe() != null && filtro.getPrecoAte() != null
                && filtro.getPrecoDe().compareTo(filtro.getPrecoAte()) > 0) {
            RestauranteFiltro ajustado = new RestauranteFiltro();
            ajustado.setNome(filtro.getNome());
            ajustado.setCidade(filtro.getCidade());
            ajustado.setBairro(filtro.getBairro());
            ajustado.setTipoCulinariaId(filtro.getTipoCulinariaId());
            ajustado.setPrecoDe(filtro.getPrecoAte());
            ajustado.setPrecoAte(filtro.getPrecoDe());
            return ajustado;
        }
        return filtro;
    }

    /**
     * Recalcula a faixa de preço do restaurante: menor e maior preço entre os pratos do cardápio.
     * Chamado sempre que um prato é criado, alterado ou excluído. Sem pratos, a faixa fica vazia.
     */
    @Transactional
    public void recalcularFaixaPreco(Long restauranteId) {
        Restaurante restaurante = buscarPorId(restauranteId);
        Object[] linha = pratoRepository.calcularFaixaPreco(restauranteId).get(0);
        restaurante.definirFaixaPreco((BigDecimal) linha[0], (BigDecimal) linha[1]);
    }

    @Transactional(readOnly = true)
    public Restaurante buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
    }

    @Transactional(readOnly = true)
    public RestauranteForm montarFormulario(Long id) {
        Restaurante restaurante = buscarPorId(id);
        Endereco endereco = restaurante.getEndereco();

        RestauranteForm form = new RestauranteForm();
        form.setId(restaurante.getId());
        form.setNome(restaurante.getNome());
        form.setCnpj(restaurante.getCnpj());
        form.setCnae(restaurante.getCnae());
        form.setTelefone(restaurante.getTelefone());
        form.setVerificacao(restaurante.isVerificacao());
        form.setLogradouro(endereco.getLogradouro());
        form.setBairro(endereco.getBairro());
        form.setCidade(endereco.getCidade());
        form.setUf(endereco.getUf());
        form.setCep(endereco.getCep());
        form.setComplemento(endereco.getComplemento());
        form.setDonoId(restaurante.getDono().getId());
        form.setTiposCulinariaIds(new ArrayList<>(
                restaurante.getTiposCulinaria().stream().map(TipoCulinaria::getId).toList()));
        return form;
    }

    /**
     * Salva pelo CRUD. O ADMIN define o dono e a verificação. Quando quem edita é o próprio dono
     * ({@code admin} falso), o dono e a verificação do restaurante ficam como estavam.
     */
    @Transactional
    public void salvar(RestauranteForm form, boolean admin) {
        if (form.getId() == null) {
            // criar restaurante pelo CRUD é coisa do ADMIN, que escolhe o dono
            Usuario dono = buscarDono(form.getDonoId());
            criar(form, dono, form.isVerificacao());
            return;
        }

        String cnpj = Normalizador.apenasAlfanumericos(form.getCnpj()).toUpperCase();
        validarCnpjUnico(cnpj, form.getId());
        List<TipoCulinaria> tipos = buscarTiposCulinaria(form.getTiposCulinariaIds());

        Restaurante restaurante = buscarPorId(form.getId());
        Usuario dono = admin ? buscarDono(form.getDonoId()) : restaurante.getDono();
        boolean verificacao = admin ? form.isVerificacao() : restaurante.isVerificacao();

        restaurante.atualizar(cnpj, Normalizador.apenasDigitos(form.getCnae()), form.getNome().trim(),
                Normalizador.apenasDigitos(form.getTelefone()), verificacao, montarEndereco(form), dono);
        restaurante.definirTiposCulinaria(tipos);
    }

    /**
     * Cria o restaurante (com o cardápio dele) para o dono informado.
     * Usado pelo CRUD do ADMIN e pelo cadastro de dono.
     */
    @Transactional
    public Restaurante criar(RestauranteForm form, Usuario dono, boolean verificacao) {
        String cnpj = Normalizador.apenasAlfanumericos(form.getCnpj()).toUpperCase();
        validarCnpjUnico(cnpj, null);
        List<TipoCulinaria> tipos = buscarTiposCulinaria(form.getTiposCulinariaIds());

        Restaurante restaurante = new Restaurante(
                cnpj,
                Normalizador.apenasDigitos(form.getCnae()),
                form.getNome().trim(),
                Normalizador.apenasDigitos(form.getTelefone()),
                verificacao,
                montarEndereco(form),
                dono);
        restaurante.definirTiposCulinaria(tipos);
        restauranteRepository.save(restaurante);
        // todo restaurante nasce com exatamente um cardápio (e sem faixa de preço, que vem dos pratos)
        cardapioRepository.save(new Cardapio(restaurante));
        return restaurante;
    }

    /** Um dono cadastra mais um restaurante para si. Nasce não verificado: quem confirma é o ADMIN. */
    @Transactional
    public Restaurante criarParaDono(RestauranteForm form, Long donoId) {
        return criar(form, buscarDono(donoId), false);
    }

    /** ADMIN confirma (ou desfaz a confirmação de) um restaurante direto da listagem. */
    @Transactional
    public boolean alternarVerificacao(Long id) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.atualizar(restaurante.getCnpj(), restaurante.getCnae(), restaurante.getNome(),
                restaurante.getTelefone(), !restaurante.isVerificacao(), restaurante.getEndereco(), restaurante.getDono());
        return restaurante.isVerificacao();
    }

    /**
     * Exclui o restaurante junto com tudo que depende dele.
     * A ordem importa: primeiro o que aponta para o restaurante, por último o restaurante.
     */
    @Transactional
    public void excluir(Long id) {
        Restaurante restaurante = buscarPorId(id);
        // fotos enviadas (mídias e pratos) ficam na tabela imagem e saem junto, na mesma transação
        List<String> arquivos = new ArrayList<>();
        midiaRepository.findByRestauranteIdOrderByIdAsc(id).forEach(midia -> arquivos.add(midia.getUrl()));
        pratoRepository.buscarPorRestaurante(id, "").forEach(prato -> arquivos.add(prato.getFoto()));
        try {
            composicaoRepository.deleteByOfertaMenuRestauranteId(id);
            ofertaMenuRepository.deleteByRestauranteId(id);
            favoritaRepository.deleteByRestauranteId(id);
            avaliacaoRepository.deleteByRestauranteId(id);
            pratoRepository.deleteByCategoriaCardapioRestauranteId(id);
            categoriaRepository.deleteByCardapioRestauranteId(id);
            cardapioRepository.deleteByRestauranteId(id);
            horarioRepository.deleteByRestauranteId(id);
            midiaRepository.deleteByRestauranteId(id);
            restauranteRepository.delete(restaurante);
            restauranteRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir \"" + restaurante.getNome() + "\": ainda existem registros ligados a ele.");
        }
        arquivos.forEach(uploadService::excluir);
    }

    private Endereco montarEndereco(RestauranteForm form) {
        String complemento = form.getComplemento() == null || form.getComplemento().isBlank()
                ? null
                : form.getComplemento().trim();
        return new Endereco(
                form.getLogradouro().trim(),
                form.getBairro().trim(),
                form.getCidade().trim(),
                form.getUf().trim().toUpperCase(),
                Normalizador.apenasDigitos(form.getCep()),
                complemento);
    }

    private void validarCnpjUnico(String cnpj, Long idAtual) {
        restauranteRepository.findByCnpj(cnpj)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("cnpj", "Já existe um restaurante com este CNPJ");
                });
    }

    private Usuario buscarDono(Long donoId) {
        if (donoId == null) {
            throw new CampoInvalidoException("donoId", "Selecione o dono do restaurante");
        }
        Usuario dono = usuarioRepository.findById(donoId)
                .orElseThrow(() -> new CampoInvalidoException("donoId", "Usuário não encontrado"));
        if (dono.getPapel() != Papel.DONO) {
            throw new CampoInvalidoException("donoId", "O responsável precisa ser um usuário com papel DONO");
        }
        return dono;
    }

    private List<TipoCulinaria> buscarTiposCulinaria(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CampoInvalidoException("tiposCulinariaIds", "Selecione ao menos um tipo de culinária");
        }
        List<TipoCulinaria> tipos = tipoCulinariaRepository.findAllById(ids);
        if (tipos.size() != new HashSet<>(ids).size()) {
            throw new CampoInvalidoException("tiposCulinariaIds", "Tipo de culinária inválido");
        }
        return tipos;
    }
}
