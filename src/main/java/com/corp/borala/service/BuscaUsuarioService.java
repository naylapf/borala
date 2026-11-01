package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.BuscaUsuarioForm;
import com.corp.borala.form.RestauranteFiltro;
import com.corp.borala.model.BuscaUsuario;
import com.corp.borala.model.Usuario;
import com.corp.borala.repository.BuscaUsuarioRepository;
import com.corp.borala.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Histórico de buscas de um usuário: listar, registrar e apagar. */
@Service
public class BuscaUsuarioService {

    private final BuscaUsuarioRepository buscaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public BuscaUsuarioService(BuscaUsuarioRepository buscaUsuarioRepository, UsuarioRepository usuarioRepository) {
        this.buscaUsuarioRepository = buscaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<BuscaUsuario> listar(Long usuarioId) {
        return buscaUsuarioRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }

    @Transactional
    public void registrar(Long usuarioId, BuscaUsuarioForm form) {
        String palavraChave = textoOuNulo(form.getPalavraChave());
        String filtros = textoOuNulo(form.getFiltros());
        if (palavraChave == null && filtros == null) {
            throw new CampoInvalidoException("palavraChave", "Informe a palavra-chave ou os filtros");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        buscaUsuarioRepository.save(new BuscaUsuario(usuario, LocalDateTime.now(), palavraChave, filtros));
    }

    /**
     * Registra sozinha a busca feita na página inicial. O nome pesquisado vira a palavra-chave
     * e os demais filtros viram um texto, ex.: "cidade=Campo Grande; culinária=Italiana; preço=de 20 até 80".
     */
    @Transactional
    public void registrarBusca(Long usuarioId, RestauranteFiltro filtro, String nomeCulinaria) {
        List<String> partes = new ArrayList<>();
        if (filtro.getCidade() != null && !filtro.getCidade().isBlank()) {
            partes.add("cidade=" + filtro.getCidade().trim());
        }
        if (filtro.getBairro() != null && !filtro.getBairro().isBlank()) {
            partes.add("bairro=" + filtro.getBairro().trim());
        }
        if (nomeCulinaria != null) {
            partes.add("culinária=" + nomeCulinaria);
        }
        if (filtro.getPrecoDe() != null) {
            partes.add("preço de=" + filtro.getPrecoDe().toPlainString());
        }
        if (filtro.getPrecoAte() != null) {
            partes.add("preço até=" + filtro.getPrecoAte().toPlainString());
        }
        String palavraChave = limitar(textoOuNulo(filtro.getNome()), 100);
        String filtros = limitar(partes.isEmpty() ? null : String.join("; ", partes), 500);
        if (palavraChave == null && filtros == null) {
            return;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        buscaUsuarioRepository.save(new BuscaUsuario(usuario, LocalDateTime.now(), palavraChave, filtros));
    }

    private String limitar(String texto, int maximo) {
        return (texto != null && texto.length() > maximo) ? texto.substring(0, maximo) : texto;
    }

    @Transactional
    public void excluir(Long usuarioId, Long id) {
        BuscaUsuario busca = buscaUsuarioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Busca não encontrada"));
        buscaUsuarioRepository.delete(busca);
    }

    /** Apaga todo o histórico de buscas do usuário. */
    @Transactional
    public void limpar(Long usuarioId) {
        buscaUsuarioRepository.deleteByUsuarioId(usuarioId);
    }

    private String textoOuNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
