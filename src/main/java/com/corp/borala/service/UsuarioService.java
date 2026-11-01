package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.exception.RegraDeNegocioException;
import com.corp.borala.form.UsuarioForm;
import com.corp.borala.model.Papel;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.AvaliacaoRepository;
import com.corp.borala.repository.BuscaUsuarioRepository;
import com.corp.borala.repository.FavoritaRepository;
import com.corp.borala.repository.UsuarioRepository;
import com.corp.borala.util.Normalizador;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** CRUD de usuários. A senha é sempre criptografada antes de ir para o banco. */
@Service
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 8;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoritaRepository favoritaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final BuscaUsuarioRepository buscaUsuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          FavoritaRepository favoritaRepository,
                          AvaliacaoRepository avaliacaoRepository,
                          BuscaUsuarioRepository buscaUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.favoritaRepository = favoritaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.buscaUsuarioRepository = buscaUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return buscar(id);
    }

    /** Usuários que podem ser donos de restaurante. */
    @Transactional(readOnly = true)
    public List<Usuario> listarDonos() {
        return usuarioRepository.findByPapelOrderByNomeAsc(Papel.DONO);
    }

    /** Monta o formulário de edição. A senha nunca é devolvida: o campo vai em branco. */
    @Transactional(readOnly = true)
    public UsuarioForm montarFormulario(Long id) {
        Usuario usuario = buscar(id);
        UsuarioForm form = new UsuarioForm();
        form.setId(usuario.getId());
        form.setNome(usuario.getNome());
        form.setEmail(usuario.getEmail());
        form.setTelefone(usuario.getTelefone());
        form.setPapel(usuario.getPapel());
        return form;
    }

    @Transactional
    public void salvar(UsuarioForm form) {
        String nome = form.getNome().trim();
        String email = form.getEmail().trim().toLowerCase();
        String telefone = Normalizador.apenasDigitos(form.getTelefone());
        boolean senhaInformada = form.getSenha() != null && !form.getSenha().isBlank();

        validarEmailUnico(email, form.getId());
        validarSenha(form.getSenha(), form.getConfirmarSenha(), form.getId() == null);

        if (form.getId() == null) {
            String senhaCriptografada = passwordEncoder.encode(form.getSenha());
            usuarioRepository.save(new Usuario(nome, email, telefone, form.getPapel(), senhaCriptografada));
        } else {
            Usuario usuario = buscar(form.getId());
            usuario.atualizarDados(nome, email, telefone, form.getPapel());
            if (senhaInformada) {
                usuario.alterarSenha(passwordEncoder.encode(form.getSenha()));
            }
        }
    }

    /**
     * Cria um usuário já validando e-mail único, senha e confirmação da senha.
     * Usado pelos cadastros de cliente e de dono (o papel nunca vem do formulário público).
     */
    @Transactional
    public Usuario criar(String nome, String email, String telefone, Papel papel, String senha, String confirmarSenha) {
        String emailPadronizado = email.trim().toLowerCase();
        validarEmailUnico(emailPadronizado, null);
        validarSenha(senha, confirmarSenha, true);
        return usuarioRepository.save(new Usuario(
                nome.trim(), emailPadronizado, Normalizador.apenasDigitos(telefone), papel,
                passwordEncoder.encode(senha)));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscar(id);
        try {
            // o que pertence só ao usuário é apagado junto; restaurantes que ele gerencia impedem a exclusão
            favoritaRepository.deleteByUsuarioId(id);
            avaliacaoRepository.deleteByUsuarioId(id);
            buscaUsuarioRepository.deleteByUsuarioId(id);
            usuarioRepository.delete(usuario);
            usuarioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir \"" + usuario.getNome() + "\": o usuário gerencia restaurantes ou tem registros ligados a ele.");
        }
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private void validarEmailUnico(String email, Long idAtual) {
        usuarioRepository.findByEmail(email)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("email", "Este e-mail já está cadastrado");
                });
    }

    /**
     * Regras da senha: obrigatória ao criar; ao editar, em branco significa "manter a atual".
     * Quando há senha, precisa ter o tamanho mínimo e ser igual à confirmação.
     */
    private void validarSenha(String senha, String confirmarSenha, boolean obrigatoria) {
        boolean senhaInformada = senha != null && !senha.isBlank();
        if (obrigatoria && !senhaInformada) {
            throw new CampoInvalidoException("senha", "Informe a senha");
        }
        if (!senhaInformada) {
            return;
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new CampoInvalidoException("senha", "A senha deve ter ao menos " + TAMANHO_MINIMO_SENHA + " caracteres");
        }
        if (confirmarSenha == null || confirmarSenha.isEmpty()) {
            throw new CampoInvalidoException("confirmarSenha", "Confirme a senha");
        }
        if (!senha.equals(confirmarSenha)) {
            throw new CampoInvalidoException("confirmarSenha", "As senhas não conferem");
        }
    }
}
