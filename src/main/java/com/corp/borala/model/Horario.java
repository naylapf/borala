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

import java.time.LocalTime;

/**
 * Horário de funcionamento do restaurante (entidade fraca).
 * O "diahora" do DER foi dividido em dia da semana + hora de abertura; o fechamento pode ser
 * depois da meia-noite (ex.: abre 11:00 e fecha 01:00). Um dia pode ter mais de um horário (almoço e jantar).
 */
@Entity
@Table(name = "horario", uniqueConstraints = @UniqueConstraint(columnNames = {"restaurante_id", "dia_semana", "abertura"}))
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;

    @Column(name = "abertura", nullable = false)
    private LocalTime abertura;

    @Column(name = "fechamento", nullable = false)
    private LocalTime fechamento;

    protected Horario() {
        // exigido pelo JPA
    }

    public Horario(Restaurante restaurante, DiaSemana diaSemana, LocalTime abertura, LocalTime fechamento) {
        this.restaurante = restaurante;
        atualizar(diaSemana, abertura, fechamento);
    }

    public void atualizar(DiaSemana diaSemana, LocalTime abertura, LocalTime fechamento) {
        this.diaSemana = diaSemana;
        this.abertura = abertura;
        this.fechamento = fechamento;
    }

    public Long getId() {
        return id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getAbertura() {
        return abertura;
    }

    public LocalTime getFechamento() {
        return fechamento;
    }
}
