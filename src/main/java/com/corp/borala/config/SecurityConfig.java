package com.corp.borala.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Login por e-mail e senha e proteção das telas por papel:
 * <ul>
 *   <li>público: página inicial (busca), login, cadastros e estilos;</li>
 *   <li>ADMIN: usuários, tipos de culinária, modalidades, lista e criação de restaurantes;</li>
 *   <li>DONO e ADMIN: telas de um restaurante (a posse do restaurante é checada em
 *       {@link com.corp.borala.security.AcessoRestauranteInterceptor});</li>
 *   <li>qualquer outra tela exige estar logado.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtrosDeSeguranca(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(autorizacao -> autorizacao
                .requestMatchers("/", "/login", "/cadastro/**", "/css/**", "/img/**", "/fotos/**", "/error", "/error/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurantes/*/ver").permitAll()
                .requestMatchers("/meu/**").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.POST, "/restaurantes/*/verificar").hasRole("ADMIN")
                .requestMatchers("/usuarios/**", "/tipos-culinaria/**", "/modalidades-menu/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/restaurantes", "/restaurantes/novo").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/restaurantes/*/excluir").hasRole("ADMIN")
                .requestMatchers("/restaurantes/**").hasAnyRole("ADMIN", "DONO")
                .requestMatchers("/dono/**").hasRole("DONO")
                .anyRequest().authenticated());

        http.formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .defaultSuccessUrl("/painel")
                .failureUrl("/login?erro")
                .permitAll());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?saiu"));

        return http.build();
    }

    /** Criptografa as senhas antes de salvar no banco. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
