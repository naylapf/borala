package com.corp.borala.util;

/** Funções para padronizar textos antes de salvar no banco. */
public final class Normalizador {

    private Normalizador() {
    }

    /** Mantém só os dígitos: "(67) 99999-9999" vira "67999999999". */
    public static String apenasDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    /** Mantém só letras e números (o novo CNPJ pode ter letras): "12.345.678/0001-95" vira "12345678000195". */
    public static String apenasAlfanumericos(String texto) {
        return texto == null ? "" : texto.replaceAll("[^A-Za-z0-9]", "");
    }
}
