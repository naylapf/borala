package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/** Relacionamento "Avalia" do DER: nota e comentário de um usuário sobre um restaurante (uma avaliação por par). */
@Entity
@Table(name = "avaliacao", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "restaurante_id"}))
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    /** De 1 a 5. */
    @Column(nullable = false)
    private Integer nota;

    /** Opcional. */
    @Column(length = 500)
    private String comentario;

    /** Dia em que a avaliação foi feita (ou atualizada pela última vez). */
    @Column(name = "data", nullable = false)
    private LocalDate data;

    protected Avaliacao() {
        // exigido pelo JPA
    }

    public Avaliacao(Usuario usuario, Restaurante restaurante, Integer nota, String comentario, LocalDate data) {
        this.usuario = usuario;
        atualizar(restaurante, nota, comentario, data);
    }

    public void atualizar(Restaurante restaurante, Integer nota, String comentario, LocalDate data) {
        this.restaurante = restaurante;
        this.nota = nota;
        this.comentario = comentario;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDate getData() {
        return data;
    }
}
