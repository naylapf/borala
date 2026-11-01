package com.corp.borala.model;

/** Dia da semana do horário de funcionamento. A ordem do enum é a ordem de exibição (segunda a domingo). */
public enum DiaSemana {
    SEGUNDA("Segunda-feira"),
    TERCA("Terça-feira"),
    QUARTA("Quarta-feira"),
    QUINTA("Quinta-feira"),
    SEXTA("Sexta-feira"),
    SABADO("Sábado"),
    DOMINGO("Domingo");

    private final String rotulo;

    DiaSemana(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Texto mostrado nas telas. */
    public String getRotulo() {
        return rotulo;
    }
}
