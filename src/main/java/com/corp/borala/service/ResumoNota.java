package com.corp.borala.service;

import java.util.Locale;

/** Nota média e quantidade de avaliações de um restaurante. */
public class ResumoNota {

    private final double media;
    private final long total;

    public ResumoNota(double media, long total) {
        this.media = media;
        this.total = total;
    }

    public double getMedia() {
        return media;
    }

    public long getTotal() {
        return total;
    }

    /** Média com uma casa e vírgula, ex.: "4,3". */
    public String getMediaTexto() {
        return String.format(new Locale("pt", "BR"), "%.1f", media);
    }

    /** Média arredondada para o número de estrelas cheias (0 a 5). */
    public int getEstrelas() {
        return (int) Math.round(media);
    }
}
