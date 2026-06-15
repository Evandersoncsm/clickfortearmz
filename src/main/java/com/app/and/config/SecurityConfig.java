package com.app.and.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Segurança da aplicação: login por formulário com um usuário padrão.
 * As credenciais podem ser sobrescritas por variáveis de ambiente
 * (APP_AUTH_USERNAME / APP_AUTH_PASSWORD) — recomendado em produção,
 * já que o repositório é público.
 */
@Configuration
public class SecurityConfig {

    private final String username;
    private final String password;

    public SecurityConfig(
            @Value("${app.auth.username}") String username,
            @Value("${app.auth.password}") String password) {
        this.username = username;
        this.password = password;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername(username)
                .password(encoder.encode(password))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Área protegida: administração das credenciais e console do banco
                        .requestMatchers("/integracoes/**", "/h2-console/**").authenticated()
                        // Relatórios e protótipos Omie ficam disponíveis sem login.
                        .requestMatchers("/omie/**").permitAll()
                        // Contrato HTTP e interface Swagger ficam públicos para consulta.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Demais telas (index, produção, relatório, importações) são públicas
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                // H2 console usa frames e POST próprio; libera só para esse caminho
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
