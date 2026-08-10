package com.tienda;

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

@Configuration
public class SecurityConfig {

    public static final String[] PUBLIC_URLS = {
        "/", "/index", "/fav/**", "/carrito/**", "/consultas/**",
        "/js/**", "/webjars/**", "/login", "/acceso_denegado"
    };

    public static final String[] USUARIO_URLS = {
        "/facturar/carrito"
    };

    public static final String[] ADMIN_OR_VENDEDOR_URLS = {
        "/producto/listado", "/categoria/listado", "/usuario/listado"
    };

    public static final String[] ADMIN_URLS = {
        "/producto/nuevo", "/producto/guardar", "/producto/modificar/**", "/producto/eliminar/**",
        "/categoria/nuevo", "/categoria/guardar", "/categoria/modificar/**", "/categoria/eliminar/**",
        "/usuario/nuevo", "/usuario/guardar", "/usuario/modificar/**", "/usuario/eliminar/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(USUARIO_URLS).hasRole("USUARIO")
                .requestMatchers(ADMIN_OR_VENDEDOR_URLS).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        http.exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        );

        http.sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Este método será reemplazado la siguiente semana
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.builder()
                .username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.builder()
                .username("rebeca")
                .password(passwordEncoder.encode("456"))
                .roles("VENDEDOR")
                .build();

        UserDetails user3 = User.builder()
                .username("pedro")
                .password(passwordEncoder.encode("789"))
                .roles("USUARIO")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }
}
