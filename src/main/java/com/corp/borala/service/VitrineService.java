package com.corp.borala.service;

import com.corp.borala.form.RestauranteFiltro;
import com.corp.borala.model.Avaliacao;
import com.corp.borala.model.Horario;
import com.corp.borala.model.Midia;
import com.corp.borala.model.OfertaMenu;
import com.corp.borala.model.Prato;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.repository.AvaliacaoRepository;
import com.corp.borala.repository.BuscaUsuarioRepository;
import com.corp.borala.repository.FavoritaRepository;
import com.corp.borala.repository.HorarioRepository;
import com.corp.borala.repository.MidiaRepository;
import com.corp.borala.repository.OfertaMenuRepository;
import com.corp.borala.repository.PratoRepository;
import com.corp.borala.repository.RestauranteRepository;
import com.corp.borala.repository.TipoCulinariaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Dados de exibição pública: nota média, foto de capa, destaques e recomendações da página inicial
 * e as informações da página de um restaurante (horários, ofertas, mídias, cardápio e avaliações).
 */
@Service
public class VitrineService {

    private static final int LIMITE_SECAO = 4;
    private static final Pattern CULINARIA_NA_BUSCA = Pattern.compile("culinária=([^;]+)");

    private final AvaliacaoRepository avaliacaoRepository;
    private final FavoritaRepository favoritaRepository;
    private final MidiaRepository midiaRepository;
    private final HorarioRepository horarioRepository;
    private final OfertaMenuRepository ofertaMenuRepository;
    private final PratoRepository pratoRepository;
    private final RestauranteRepository restauranteRepository;
    private final TipoCulinariaRepository tipoCulinariaRepository;
    private final BuscaUsuarioRepository buscaUsuarioRepository;
    private final RestauranteService restauranteService;

    public VitrineService(AvaliacaoRepository avaliacaoRepository,
                          FavoritaRepository favoritaRepository,
                          MidiaRepository midiaRepository,
                          HorarioRepository horarioRepository,
                          OfertaMenuRepository ofertaMenuRepository,
                          PratoRepository pratoRepository,
                          RestauranteRepository restauranteRepository,
                          TipoCulinariaRepository tipoCulinariaRepository,
                          BuscaUsuarioRepository buscaUsuarioRepository,
                          RestauranteService restauranteService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.favoritaRepository = favoritaRepository;
        this.midiaRepository = midiaRepository;
        this.horarioRepository = horarioRepository;
        this.ofertaMenuRepository = ofertaMenuRepository;
        this.pratoRepository = pratoRepository;
        this.restauranteRepository = restauranteRepository;
        this.tipoCulinariaRepository = tipoCulinariaRepository;
        this.buscaUsuarioRepository = buscaUsuarioRepository;
        this.restauranteService = restauranteService;
    }

    // ---------- cartões (nota média e capa) ----------

    /** Nota média por id de restaurante; quem não tem avaliações não aparece no mapa. */
    @Transactional(readOnly = true)
    public Map<Long, ResumoNota> notas(Collection<Restaurante> restaurantes) {
        List<Long> ids = ids(restaurantes);
        Map<Long, ResumoNota> notas = new HashMap<>();
        if (ids.isEmpty()) {
            return notas;
        }
        for (Object[] linha : avaliacaoRepository.resumoPorRestaurante(ids)) {
            notas.put((Long) linha[0], new ResumoNota(((Number) linha[1]).doubleValue(), (Long) linha[2]));
        }
        return notas;
    }

    /** Primeira foto de cada restaurante (a capa do cartão); restaurantes sem foto não aparecem no mapa. */
    @Transactional(readOnly = true)
    public Map<Long, String> capas(Collection<Restaurante> restaurantes) {
        List<Long> ids = ids(restaurantes);
        Map<Long, String> capas = new HashMap<>();
        if (ids.isEmpty()) {
            return capas;
        }
        for (Midia foto : midiaRepository.fotosDosRestaurantes(ids)) {
            capas.putIfAbsent(foto.getRestaurante().getId(), foto.getUrl());
        }
        return capas;
    }

    // ---------- página inicial ----------

    /** Restaurantes mais bem avaliados (só entra quem tem ao menos uma avaliação). */
    @Transactional(readOnly = true)
    public List<Restaurante> destaques() {
        return naOrdem(avaliacaoRepository.idsMelhorAvaliados(PageRequest.of(0, LIMITE_SECAO)));
    }

    /**
     * Recomendações para o cliente logado: restaurantes da culinária que ele mais pesquisou.
     * Sem histórico de busca, mostra os mais favoritados; sem nenhum dos dois, a lista vem vazia.
     */
    @Transactional(readOnly = true)
    public Recomendacao recomendados(Long usuarioId) {
        Optional<TipoCulinaria> preferida = culinariaMaisBuscada(usuarioId)
                .flatMap(tipoCulinariaRepository::findByNomeIgnoreCase);
        if (preferida.isPresent()) {
            RestauranteFiltro filtro = new RestauranteFiltro();
            filtro.setTipoCulinariaId(preferida.get().getId());
            List<Restaurante> lista = restauranteService.buscar(filtro).stream().limit(LIMITE_SECAO).toList();
            if (!lista.isEmpty()) {
                return new Recomendacao("Recomendados para você",
                        "Porque você costuma buscar culinária " + preferida.get().getNome() + ".", lista);
            }
        }
        List<Restaurante> maisFavoritados = naOrdem(favoritaRepository.idsMaisFavoritados(PageRequest.of(0, LIMITE_SECAO)));
        if (!maisFavoritados.isEmpty()) {
            return new Recomendacao("Os mais favoritados",
                    "Ainda não temos o seu histórico de buscas. Estes são os preferidos da galera.", maisFavoritados);
        }
        return new Recomendacao("Recomendados para você",
                "Faça sua primeira busca ou favorite um restaurante e a gente passa a sugerir lugares com a sua cara.",
                List.of());
    }

    /** Para cada culinária que tem restaurantes: até 4 deles (as seções "Comida ..." da página inicial). */
    @Transactional(readOnly = true)
    public Map<TipoCulinaria, List<Restaurante>> porCulinaria(List<TipoCulinaria> tipos) {
        Map<TipoCulinaria, List<Restaurante>> secoes = new LinkedHashMap<>();
        for (TipoCulinaria tipo : tipos) {
            RestauranteFiltro filtro = new RestauranteFiltro();
            filtro.setTipoCulinariaId(tipo.getId());
            List<Restaurante> lista = restauranteService.buscar(filtro);
            if (!lista.isEmpty()) {
                secoes.put(tipo, lista.stream().limit(4).toList());
            }
        }
        return secoes;
    }

    /** Culinária que mais aparece nos filtros do histórico de buscas do usuário. */
    private Optional<String> culinariaMaisBuscada(Long usuarioId) {
        Map<String, Long> contagem = new HashMap<>();
        buscaUsuarioRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId).forEach(busca -> {
            if (busca.getFiltros() != null) {
                Matcher achado = CULINARIA_NA_BUSCA.matcher(busca.getFiltros());
                if (achado.find()) {
                    contagem.merge(achado.group(1).trim(), 1L, Long::sum);
                }
            }
        });
        return contagem.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    // ---------- página do restaurante ----------

    /** Nota média do restaurante, ou null se ainda não foi avaliado. */
    @Transactional(readOnly = true)
    public ResumoNota resumo(Restaurante restaurante) {
        return notas(List.of(restaurante)).get(restaurante.getId());
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> avaliacoes(Long restauranteId) {
        return avaliacaoRepository.listarDoRestaurante(restauranteId);
    }

    /** Horários em ordem de dia da semana e de abertura. */
    @Transactional(readOnly = true)
    public List<Horario> horarios(Long restauranteId) {
        return horarioRepository.findByRestauranteId(restauranteId).stream()
                .sorted(Comparator.comparing((Horario h) -> h.getDiaSemana().ordinal()).thenComparing(Horario::getAbertura))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfertaMenu> ofertas(Long restauranteId) {
        return ofertaMenuRepository.findByRestauranteIdOrderByModalidadeMenuNomeAsc(restauranteId);
    }

    @Transactional(readOnly = true)
    public List<Midia> midias(Long restauranteId) {
        return midiaRepository.findByRestauranteIdOrderByIdAsc(restauranteId);
    }

    /** Pratos disponíveis agrupados por categoria, com as categorias em ordem alfabética. */
    @Transactional(readOnly = true)
    public Map<String, List<Prato>> cardapio(Long restauranteId) {
        return pratoRepository.buscarPorRestaurante(restauranteId, "").stream()
                .filter(Prato::isDisponibilidade)
                .collect(Collectors.groupingBy(prato -> prato.getCategoria().getNome(),
                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER), Collectors.toList()));
    }

    // ---------- apoio ----------

    private List<Long> ids(Collection<Restaurante> restaurantes) {
        return restaurantes.stream().map(Restaurante::getId).distinct().toList();
    }

    /** Carrega os restaurantes mantendo a ordem da lista de ids. */
    private List<Restaurante> naOrdem(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, Restaurante> porId = restauranteRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Restaurante::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        return ids.stream().map(porId::get).filter(r -> r != null).toList();
    }
}
