package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Relacionamento "Compõe" do DER: um prato faz parte de uma oferta de menu, com forma de serviço e cobrança. */
@Entity
@Table(name = "composicao", uniqueConstraints = @UniqueConstraint(columnNames = {"oferta_menu_id", "prato_id"}))
public class Composicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "oferta_menu_id", nullable = false)
    private OfertaMenu ofertaMenu;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prato_id", nullable = false)
    private Prato prato;

    /** Como o prato é cobrado dentro da oferta (texto livre por enquanto). */
    @Column(nullable = false, length = 50)
    private String cobranca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormaServico formaServico;

    protected Composicao() {
        // exigido pelo JPA
    }

    public Composicao(OfertaMenu ofertaMenu, Prato prato, String cobranca, FormaServico formaServico) {
        this.ofertaMenu = ofertaMenu;
        atualizar(prato, cobranca, formaServico);
    }

    public void atualizar(Prato prato, String cobranca, FormaServico formaServico) {
        this.prato = prato;
        this.cobranca = cobranca;
        this.formaServico = formaServico;
    }

    public Long getId() {
        return id;
    }

    public OfertaMenu getOfertaMenu() {
        return ofertaMenu;
    }

    public Prato getPrato() {
        return prato;
    }

    public String getCobranca() {
        return cobranca;
    }

    public FormaServico getFormaServico() {
        return formaServico;
    }
}
