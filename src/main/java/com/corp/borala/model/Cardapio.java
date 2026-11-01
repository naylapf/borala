package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Cardápio do restaurante (entidade fraca): cada restaurante possui exatamente um. */
@Entity
@Table(name = "cardapio")
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false, unique = true)
    private Restaurante restaurante;

    /** Data da última atualização (data_att no DER). */
    @Column(nullable = false)
    private LocalDateTime dataAtt;

    protected Cardapio() {
        // exigido pelo JPA
    }

    public Cardapio(Restaurante restaurante) {
        this.restaurante = restaurante;
        this.dataAtt = LocalDateTime.now();
    }

    /** Chamado sempre que um prato é adicionado, alterado ou removido. */
    public void registrarAtualizacao() {
        this.dataAtt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public LocalDateTime getDataAtt() {
        return dataAtt;
    }
}
