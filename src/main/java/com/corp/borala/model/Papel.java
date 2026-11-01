package com.corp.borala.model;

/**
 * Papel do usuário no sistema. Todos compartilham o mesmo login (tabela usuario).
 * ADMIN é o grupo do projeto: gerencia usuários, restaurantes e verifica cadastros.
 */
public enum Papel {
    CLIENTE,
    DONO,
    ADMIN
}
