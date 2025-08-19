package com.ckweb.rest_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ckweb.rest_api.security.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/registro", "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/registro/adm").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/usuarios/info").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/usuarios/info").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/usuarios/info").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/usuarios/{id}").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/usuarios/{id}").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/usuarios/{id}").hasRole("ADMIN") // regra geral
                .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/enderecos").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/enderecos/{id}").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/enderecos/usuarios/{id}").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/enderecos").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/enderecos/{id}").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/enderecos/info").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/enderecos/info/{id}").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/enderecos/info").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/enderecos/info").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/enderecos/info/{id}").hasAnyRole("USER", "MANAGER","ADMIN")

                .requestMatchers(HttpMethod.GET, "/produtos").permitAll()
                .requestMatchers(HttpMethod.GET, "/produtos/categoria/{categoria}").permitAll()
                .requestMatchers(HttpMethod.GET, "/produtos/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/produtos/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/produtos").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/produtos").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/produtos/{id}").hasRole("ADMIN")

                .requestMatchers("/carrinho/**").hasAnyRole("USER", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/avaliacoes").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/avaliacoes/{produtoId}").permitAll()

                .requestMatchers("/favoritos/**").hasAnyRole("USER", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/envio/cotarfrete").permitAll()
                
                .requestMatchers(HttpMethod.GET, "/pedidos/{id}").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/pedidos/usuario/{id}").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/pedidos").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/pedidos/finalizar").hasAnyRole("USER", "MANAGER", "ADMIN")

                // Rever regras de acesso a partir deste ponto
                .requestMatchers(HttpMethod.POST, "/pagamentos/webhook").permitAll()
                .requestMatchers(HttpMethod.POST, "/pagamentos").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/pagamentos/**").hasAnyRole("USER", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/envios/{pedidoId}").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/envios/{pedidoId}").hasAnyRole("MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/cupons").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/cupons").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/cupons/{codigo}").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/cupons/{id}").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/itens-pedido/**").hasAnyRole("USER", "MANAGER", "ADMIN")

                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .anyRequest().authenticated()
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
