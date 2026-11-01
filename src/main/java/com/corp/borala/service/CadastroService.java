package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.CadastroClienteForm;
import com.corp.borala.form.CadastroDonoForm;
import com.corp.borala.model.Papel;
import com.corp.borala.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastros públicos: cliente (simples) e dono (usuário + restaurante + cardápio de uma só vez). */
@Service
public class CadastroService {

    private final UsuarioService usuarioService;
    private final RestauranteService restauranteService;

    public CadastroService(UsuarioService usuarioService, RestauranteService restauranteService) {
        this.usuarioService = usuarioService;
        this.restauranteService = restauranteService;
    }

    @Transactional
    public void cadastrarCliente(CadastroClienteForm form) {
        usuarioService.criar(form.getNome(), form.getEmail(), form.getTelefone(), Papel.CLIENTE,
                form.getSenha(), form.getConfirmarSenha());
    }

    /**
     * Cria o usuário DONO e o restaurante dele. Se qualquer parte falhar (e-mail repetido, CNPJ repetido...),
     * nada é salvo. O restaurante nasce não verificado: quem verifica é o ADMIN.
     */
    @Transactional
    public void cadastrarDono(CadastroDonoForm form) {
        Usuario dono = usuarioService.criar(form.getNome(), form.getEmail(), form.getTelefone(), Papel.DONO,
                form.getSenha(), form.getConfirmarSenha());
        try {
            restauranteService.criar(form.getRestaurante(), dono, false);
        } catch (CampoInvalidoException e) {
            // o campo do restaurante fica dentro de "restaurante." no formulário de cadastro
            throw new CampoInvalidoException("restaurante." + e.getCampo(), e.getMessage());
        }
    }
}
