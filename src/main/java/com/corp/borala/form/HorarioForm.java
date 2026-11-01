package com.corp.borala.form;

import com.corp.borala.model.DiaSemana;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/** Formulário de horário de funcionamento. */
public class HorarioForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    @NotNull(message = "Selecione o dia da semana")
    private DiaSemana diaSemana;

    @NotNull(message = "Informe a hora de abertura")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime abertura;

    @NotNull(message = "Informe a hora de fechamento")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime fechamento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getAbertura() {
        return abertura;
    }

    public void setAbertura(LocalTime abertura) {
        this.abertura = abertura;
    }

    public LocalTime getFechamento() {
        return fechamento;
    }

    public void setFechamento(LocalTime fechamento) {
        this.fechamento = fechamento;
    }
}
