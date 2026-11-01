package com.corp.borala.config;

import com.corp.borala.model.ModalidadeMenu;
import com.corp.borala.model.Papel;
import com.corp.borala.model.TipoCulinaria;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.ModalidadeMenuRepository;
import com.corp.borala.repository.TipoCulinariaRepository;
import com.corp.borala.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ao iniciar a aplicação, cadastra tipos de culinária e modalidades de menu se as tabelas estiverem vazias
 * e cria o administrador inicial se ainda não existir nenhum.
 */
@Component
@Order(1)
public class CarregadorDadosIniciais implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CarregadorDadosIniciais.class);

    private static final List<String> TIPOS_CULINARIA = List.of(
            "Brasileira", "Francesa", "Italiana", "Japonesa", "Árabe", "Mexicana", "Chinesa", "Vegetariana");

    private static final List<String> MODALIDADES_MENU = List.of(
            "À la carte", "Rodízio", "Buffet", "Menu degustação");

    private final TipoCulinariaRepository tipoCulinariaRepository;
    private final ModalidadeMenuRepository modalidadeMenuRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String emailAdmin;
    private final String senhaAdmin;

    public CarregadorDadosIniciais(TipoCulinariaRepository tipoCulinariaRepository,
                                   ModalidadeMenuRepository modalidadeMenuRepository,
                                   UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${borala.admin.email}") String emailAdmin,
                                   @Value("${borala.admin.senha}") String senhaAdmin) {
        this.tipoCulinariaRepository = tipoCulinariaRepository;
        this.modalidadeMenuRepository = modalidadeMenuRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailAdmin = emailAdmin;
        this.senhaAdmin = senhaAdmin;
    }

    @Override
    public void run(String... args) {
        if (tipoCulinariaRepository.count() == 0) {
            tipoCulinariaRepository.saveAll(TIPOS_CULINARIA.stream().map(TipoCulinaria::new).toList());
        }
        if (modalidadeMenuRepository.count() == 0) {
            modalidadeMenuRepository.saveAll(MODALIDADES_MENU.stream().map(ModalidadeMenu::new).toList());
        }
        criarAdministrador();
    }

    private void criarAdministrador() {
        if (!usuarioRepository.findByPapelOrderByNomeAsc(Papel.ADMIN).isEmpty()) {
            return;
        }
        try {
            usuarioRepository.save(new Usuario("Administrador", emailAdmin.trim().toLowerCase(), "67000000000",
                    Papel.ADMIN, passwordEncoder.encode(senhaAdmin)));
            LOG.info("Administrador inicial criado: {}", emailAdmin);
        } catch (RuntimeException e) {
            // acontece se o banco ainda não aceita o papel ADMIN (script 03 não rodado) ou se o e-mail já existe
            LOG.warn("Não foi possível criar o administrador inicial. Rode o 03_ajustes_banco.sql e reinicie. Motivo: {}",
                    e.getMessage());
        }
    }
}
