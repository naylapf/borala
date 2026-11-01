package com.corp.borala.form;

import jakarta.validation.constraints.NotNull;

/** Formulário para favoritar um restaurante. */
public class FavoritaForm {

    @NotNull(message = "Selecione o restaurante")
    private Long restauranteId;

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }
}
