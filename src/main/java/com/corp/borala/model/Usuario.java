package com.corp.borala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Usuário do sistema: cliente ou dono de restaurante, conforme o papel. */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    /** Apenas dígitos, com DDD. */
    @Column(nullable = false, length = 11)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Papel papel;

    /** Sempre guardada criptografada (BCrypt), nunca em texto puro. */
    @Column(nullable = false)
    private String senha;

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String nome, String email, String telefone, Papel papel, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.papel = papel;
        this.senha = senha;
    }

    public void atualizarDados(String nome, String email, String telefone, Papel papel) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.papel = papel;
    }

    /** Recebe a senha JÁ criptografada. */
    public void alterarSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public Papel getPapel() {
        return papel;
    }

    public String getSenha() {
        return senha;
    }
}
