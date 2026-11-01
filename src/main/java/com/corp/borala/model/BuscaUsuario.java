package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Busca feita por um usuário (entidade fraca de Usuario): guarda o histórico de pesquisas. */
@Entity
@Table(name = "busca_usuario")
public class BuscaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Momento da busca (datahora no DER). */
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    /** Texto pesquisado. Opcional se houver filtros. */
    @Column(name = "palavra_chave", length = 100)
    private String palavraChave;

    /** Filtros aplicados, em texto livre por enquanto (ex.: "cidade=Campo Grande; bairro=Centro"). */
    @Column(length = 500)
    private String filtros;

    protected BuscaUsuario() {
        // exigido pelo JPA
    }

    public BuscaUsuario(Usuario usuario, LocalDateTime dataHora, String palavraChave, String filtros) {
        this.usuario = usuario;
        this.dataHora = dataHora;
        this.palavraChave = palavraChave;
        this.filtros = filtros;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getPalavraChave() {
        return palavraChave;
    }

    public String getFiltros() {
        return filtros;
    }
}
