package com.corp.borala.form;

import jakarta.validation.Valid;

/**
 * Cadastro de dono: os dados do responsável e, ao mesmo tempo, os dados do restaurante.
 * Cria o usuário (papel DONO), o restaurante e o cardápio de uma vez.
 */
public class CadastroDonoForm extends CadastroClienteForm {

    @Valid
    private RestauranteForm restaurante = new RestauranteForm();

    public RestauranteForm getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(RestauranteForm restaurante) {
        this.restaurante = restaurante;
    }
}
