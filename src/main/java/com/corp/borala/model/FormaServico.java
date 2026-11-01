package com.corp.borala.model;

/** Quem serve o prato dentro de uma oferta de menu (atributo "formaservico" do relacionamento Compõe). */
public enum FormaServico {
    SERVIDO_PELO_RESTAURANTE("Servido pelo restaurante"),
    SERVIDO_PELO_CLIENTE("Servido pelo cliente (self-service)");

    private final String rotulo;

    FormaServico(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Texto mostrado nas telas. */
    public String getRotulo() {
        return rotulo;
    }
}
