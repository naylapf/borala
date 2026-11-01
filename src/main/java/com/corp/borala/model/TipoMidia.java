package com.corp.borala.model;

/** Tipo de mídia exibida pelo restaurante. */
public enum TipoMidia {
    FOTO("Foto"),
    VIDEO("Vídeo");

    private final String rotulo;

    TipoMidia(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Texto mostrado nas telas. */
    public String getRotulo() {
        return rotulo;
    }
}
