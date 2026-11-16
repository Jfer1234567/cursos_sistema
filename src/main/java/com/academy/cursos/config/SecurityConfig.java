package com.academy.cursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/quienes-somos",
                    "/lineas-investigacion",
                    "/contacto",
                    "/catalogo/**",
                    "/cursos/**",
                    "/verificar/**",
                    "/login",
                    "/registro",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/uploads/**",
                    "/error/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/participante/**", "/inscripcion/**").hasAnyRole("PARTICIPANTE", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .addLogoutHandler(new org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler(
                    new org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter(
                        org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.CACHE,
                        org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES,
                        org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.STORAGE
                    )
                ))
                .permitAll()
            );

        return http.build();
    }
}
