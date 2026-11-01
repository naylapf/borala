package com.corp.borala.form;

import java.math.BigDecimal;

/**
 * Filtros da busca de restaurantes. Todos são opcionais e podem ser combinados.
 * Preço "de/até": aparecem os restaurantes cuja faixa de preço cruza esse intervalo.
 */
public class RestauranteFiltro {

    private String nome;
    private String cidade;
    private String bairro;
    private Long tipoCulinariaId;
    private BigDecimal precoDe;
    private BigDecimal precoAte;

    /** Verdadeiro quando nenhum filtro foi preenchido. */
    public boolean isVazio() {
        return textoVazio(nome) && textoVazio(cidade) && textoVazio(bairro)
                && tipoCulinariaId == null && precoDe == null && precoAte == null;
    }

    private static boolean textoVazio(String texto) {
        return texto == null || texto.isBlank();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Long getTipoCulinariaId() {
        return tipoCulinariaId;
    }

    public void setTipoCulinariaId(Long tipoCulinariaId) {
        this.tipoCulinariaId = tipoCulinariaId;
    }

    public BigDecimal getPrecoDe() {
        return precoDe;
    }

    public void setPrecoDe(BigDecimal precoDe) {
        this.precoDe = precoDe;
    }

    public BigDecimal getPrecoAte() {
        return precoAte;
    }

    public void setPrecoAte(BigDecimal precoAte) {
        this.precoAte = precoAte;
    }
}
