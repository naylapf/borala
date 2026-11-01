package com.corp.borala.service;

import com.corp.borala.model.Restaurante;

import java.util.List;

/** Seção "recomendados para você" da página inicial: título, explicação e restaurantes. */
public class Recomendacao {

    private final String titulo;
    private final String explicacao;
    private final List<Restaurante> restaurantes;

    public Recomendacao(String titulo, String explicacao, List<Restaurante> restaurantes) {
        this.titulo = titulo;
        this.explicacao = explicacao;
        this.restaurantes = restaurantes;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public List<Restaurante> getRestaurantes() {
        return restaurantes;
    }
}
