package com.corp.borala.config;

import com.corp.borala.form.RestauranteForm;
import com.corp.borala.model.Avaliacao;
import com.corp.borala.model.Cardapio;
import com.corp.borala.model.Categoria;
import com.corp.borala.model.DiaSemana;
import com.corp.borala.model.Horario;
import com.corp.borala.model.Midia;
import com.corp.borala.model.Papel;
import com.corp.borala.model.Prato;
import com.corp.borala.model.Restaurante;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.model.TipoMidia;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.AvaliacaoRepository;
import com.corp.borala.repository.CardapioRepository;
import com.corp.borala.repository.CategoriaRepository;
import com.corp.borala.repository.HorarioRepository;
import com.corp.borala.repository.MidiaRepository;
import com.corp.borala.repository.PratoRepository;
import com.corp.borala.repository.TipoCulinariaRepository;
import com.corp.borala.repository.UsuarioRepository;
import com.corp.borala.service.RestauranteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Dados de demonstração (restaurantes, pratos, fotos e avaliações), para apresentar o projeto sem cadastrar tudo à mão.
 * Só roda quando {@code borala.dados-demo=true} (variável de ambiente DADOS_DEMO=true) e apenas uma vez:
 * se o usuário demo já existir, nada é criado. As fotos ficam em {@code static/img/demo}.
 * Todos os dados são fictícios (CNPJs gerados, telefones e endereços genéricos).
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "borala.dados-demo", havingValue = "true")
public class CarregadorDadosDemo implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CarregadorDadosDemo.class);

    public static final String EMAIL_DONO_DEMO = "demo@borala.com";
    public static final String SENHA_DEMO = "demo12345";
    private static final String PASTA_FOTOS = "/img/demo/";

    private record PratoDemo(String nome, String descricao, String preco, int serve, String foto) {
    }

    private record RestauranteDemo(String nome, String bairro, List<String> culinarias, String foto,
                                   List<PratoDemo> pratos) {
    }

    private static final List<RestauranteDemo> RESTAURANTES = List.of(
            new RestauranteDemo("Numar", "Bela Vista", List.of("Italiana"), "numar.png", List.of(
                    new PratoDemo("Lasanha Bolonhesa", "Massa fresca, molho bolonhesa e queijo gratinado", "45.00", 1, "prato-lasanha.jpg"),
                    new PratoDemo("Espaguete ao Pesto", "Espaguete com molho pesto de manjericão", "55.00", 1, null),
                    new PratoDemo("Nhoque com Torrada Crostini", "Nhoque ao sugo com manjericão e parmesão", "45.00", 1, "prato-nhoque.jpg"),
                    new PratoDemo("Carbonara Clássico", "Espaguete, gema, queijo pecorino e bacon", "105.00", 2, "prato-carbonara.jpg"))),
            new RestauranteDemo("Fogo de Chão", "Jardins", List.of("Brasileira"), "fogo-de-chao.jpg", List.of(
                    new PratoDemo("Picanha na Brasa", "Picanha grelhada com farofa e vinagrete", "139.00", 2, null),
                    new PratoDemo("Costela Assada", "Costela bovina assada lentamente", "98.00", 2, null))),
            new RestauranteDemo("Aguzzo", "Pinheiros", List.of("Italiana"), "aguzzo.webp", List.of(
                    new PratoDemo("Risoto de Cogumelos", "Arroz arbóreo, mix de cogumelos e parmesão", "72.00", 1, null),
                    new PratoDemo("Ravióli de Queijo", "Ravióli recheado com molho de tomate fresco", "64.00", 1, null))),
            new RestauranteDemo("America Paulista", "Bela Vista", List.of("Brasileira"), "america-paulista.webp", List.of(
                    new PratoDemo("Hambúrguer Clássico", "Pão brioche, 180 g de carne e queijo", "42.00", 1, null),
                    new PratoDemo("Prato Feito Executivo", "Arroz, feijão, bife e salada", "36.00", 1, null))),
            new RestauranteDemo("Le Casserole", "Centro", List.of("Francesa"), "le-casserole.jpg", List.of(
                    new PratoDemo("Coq au Vin", "Frango cozido no vinho tinto com legumes", "89.00", 1, null),
                    new PratoDemo("Sopa de Cebola Gratinada", "Clássica sopa francesa com crouton", "38.00", 1, null))),
            new RestauranteDemo("Oue Sushi", "Jardins", List.of("Japonesa"), "oue-sushi.webp", List.of(
                    new PratoDemo("Combinado 20 Peças", "Sushis e sashimis variados", "96.00", 2, null),
                    new PratoDemo("Temaki de Salmão", "Cone de alga com salmão e cream cheese", "34.00", 1, null))),
            new RestauranteDemo("Atithi", "Consolação", List.of("Vegetariana"), "atithi.webp", List.of(
                    new PratoDemo("Curry de Grão-de-bico", "Curry suave com arroz basmati", "46.00", 1, null),
                    new PratoDemo("Samosa Vegetariano", "Pastel indiano frito recheado com batata", "22.00", 1, null))),
            new RestauranteDemo("Carrito Organic", "Vila Madalena", List.of("Vegetariana"), "carrito-organic.webp", List.of(
                    new PratoDemo("Bowl Orgânico", "Quinoa, legumes assados e homus", "39.00", 1, null),
                    new PratoDemo("Burrito Vegano", "Feijão preto, arroz, guacamole e salsa", "35.00", 1, null))),
            new RestauranteDemo("Sushimar Vegano", "Pinheiros", List.of("Japonesa", "Vegetariana"), "sushimar-vegano.webp", List.of(
                    new PratoDemo("Combinado Vegano 16 Peças", "Sushis de legumes e tofu defumado", "78.00", 2, null),
                    new PratoDemo("Hot Roll de Cogumelos", "Hot roll empanado com cogumelos", "44.00", 1, null))),
            new RestauranteDemo("Loving Hut", "Liberdade", List.of("Vegetariana"), "loving-hut.png", List.of(
                    new PratoDemo("Feijoada Vegana", "Feijão preto com proteína de soja e couve", "48.00", 2, null),
                    new PratoDemo("Strogonoff de Palmito", "Palmito ao molho cremoso com arroz e batata palha", "41.00", 1, null))),
            new RestauranteDemo("Taste and See", "Vila Mariana", List.of("Vegetariana"), "taste-and-see.jpg", List.of(
                    new PratoDemo("Moqueca de Banana-da-terra", "Moqueca vegana com arroz e farofa", "52.00", 2, null),
                    new PratoDemo("Salada da Casa", "Folhas, tomate seco e castanhas", "28.00", 1, null))),
            new RestauranteDemo("Vegano 901", "Jardins", List.of("Vegetariana"), "vegano.webp", List.of(
                    new PratoDemo("Lasanha de Abobrinha", "Abobrinha, ricota de castanhas e molho de tomate", "47.00", 1, null),
                    new PratoDemo("Hambúrguer de Grão-de-bico", "Pão integral, hambúrguer e maionese vegana", "36.00", 1, null))),
            new RestauranteDemo("Dona Augusta", "República", List.of("Vegetariana"), "dona-augusta.webp", List.of(
                    new PratoDemo("Coxinha Vegana", "Coxinha de jaca com catupiry vegetal", "12.00", 1, null),
                    new PratoDemo("Pastel de Palmito", "Pastel frito na hora", "14.00", 1, null))));

    private static final String[][] CLIENTES = {
            {"Ana Souza", "ana@demo.com"}, {"Bruno Lima", "bruno@demo.com"}, {"Carla Mendes", "carla@demo.com"}};

    private static final String[] COMENTARIOS = {
            "Ótimo atendimento e comida deliciosa!", "Ambiente agradável, voltarei com certeza.",
            "Bom custo-benefício.", "Pratos bem servidos e saborosos.", "Muito bom, recomendo."};

    private final UsuarioRepository usuarioRepository;
    private final TipoCulinariaRepository tipoCulinariaRepository;
    private final RestauranteService restauranteService;
    private final CardapioRepository cardapioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PratoRepository pratoRepository;
    private final MidiaRepository midiaRepository;
    private final HorarioRepository horarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public CarregadorDadosDemo(UsuarioRepository usuarioRepository,
                               TipoCulinariaRepository tipoCulinariaRepository,
                               RestauranteService restauranteService,
                               CardapioRepository cardapioRepository,
                               CategoriaRepository categoriaRepository,
                               PratoRepository pratoRepository,
                               MidiaRepository midiaRepository,
                               HorarioRepository horarioRepository,
                               AvaliacaoRepository avaliacaoRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tipoCulinariaRepository = tipoCulinariaRepository;
        this.restauranteService = restauranteService;
        this.cardapioRepository = cardapioRepository;
        this.categoriaRepository = categoriaRepository;
        this.pratoRepository = pratoRepository;
        this.midiaRepository = midiaRepository;
        this.horarioRepository = horarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail(EMAIL_DONO_DEMO).isPresent()) {
            return;
        }
        String senha = passwordEncoder.encode(SENHA_DEMO);
        Usuario dono = usuarioRepository.save(
                new Usuario("Dono Demonstração", EMAIL_DONO_DEMO, "11999990000", Papel.DONO, senha));
        List<Usuario> clientes = java.util.Arrays.stream(CLIENTES)
                .map(c -> usuarioRepository.save(new Usuario(c[0], c[1], "11999990001", Papel.CLIENTE, senha)))
                .toList();

        int indice = 0;
        for (RestauranteDemo demo : RESTAURANTES) {
            Restaurante restaurante = criarRestaurante(demo, dono, indice);
            criarCardapio(restaurante, demo);
            criarHorarios(restaurante);
            criarAvaliacoes(restaurante, clientes, indice);
            indice++;
        }
        LOG.info("Dados de demonstração criados: {} restaurantes. Dono demo: {} / {} (clientes: ana@demo.com etc., mesma senha)",
                RESTAURANTES.size(), EMAIL_DONO_DEMO, SENHA_DEMO);
    }

    private Restaurante criarRestaurante(RestauranteDemo demo, Usuario dono, int indice) {
        RestauranteForm form = new RestauranteForm();
        form.setNome(demo.nome());
        form.setCnpj(cnpjFicticio(indice));
        form.setCnae("5611201");
        form.setTelefone("1133334444");
        form.setLogradouro("Av. Paulista, s/n");
        form.setBairro(demo.bairro());
        form.setCidade("São Paulo");
        form.setUf("SP");
        form.setCep("01310100");
        form.setTiposCulinariaIds(demo.culinarias().stream()
                .map(nome -> tipoCulinariaRepository.findByNomeIgnoreCase(nome).map(TipoCulinaria::getId).orElseThrow())
                .toList());
        Restaurante restaurante = restauranteService.criar(form, dono, true);
        midiaRepository.save(new Midia(restaurante, PASTA_FOTOS + demo.foto(), TipoMidia.FOTO));
        return restaurante;
    }

    private void criarCardapio(Restaurante restaurante, RestauranteDemo demo) {
        Cardapio cardapio = cardapioRepository.findByRestauranteId(restaurante.getId()).orElseThrow();
        Categoria categoria = categoriaRepository.save(new Categoria("Pratos principais", cardapio));
        TipoCulinaria tipo = tipoCulinariaRepository.findByNomeIgnoreCase(demo.culinarias().get(0)).orElseThrow();
        for (PratoDemo prato : demo.pratos()) {
            String foto = prato.foto() == null ? null : PASTA_FOTOS + prato.foto();
            pratoRepository.save(new Prato(prato.nome(), prato.descricao(), new BigDecimal(prato.preco()), true,
                    prato.serve(), foto, categoria, tipo));
        }
        restauranteService.recalcularFaixaPreco(restaurante.getId());
    }

    private void criarHorarios(Restaurante restaurante) {
        for (DiaSemana dia : DiaSemana.values()) {
            boolean domingo = dia == DiaSemana.DOMINGO;
            horarioRepository.save(new Horario(restaurante, dia,
                    LocalTime.of(domingo ? 12 : 11, 0), LocalTime.of(domingo ? 22 : 23, 0)));
        }
    }

    /** Cada cliente avalia metade dos restaurantes, com notas de 3 a 5, para variar as estrelas. */
    private void criarAvaliacoes(Restaurante restaurante, List<Usuario> clientes, int indice) {
        for (int c = 0; c < clientes.size(); c++) {
            if ((indice + c) % 2 == 0) {
                int nota = 3 + ((indice * 7 + c * 3) % 3);
                avaliacaoRepository.save(new Avaliacao(clientes.get(c), restaurante, nota,
                        COMENTARIOS[(indice + c) % COMENTARIOS.length], LocalDate.now().minusDays(indice + c + 1L)));
            }
        }
    }

    /** CNPJ de 14 dígitos com dígitos verificadores corretos (base 2000000N + filial 0001). */
    private static String cnpjFicticio(int indice) {
        String base = String.format("%08d0001", 20000000 + indice);
        int primeiro = digito(base, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int segundo = digito(base + primeiro, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return base + primeiro + segundo;
    }

    private static int digito(String numero, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (numero.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
