package com.Market.MeatShop.Security.Config;

import com.Market.MeatShop.Security.Filters.JwtFilter;
import com.Market.MeatShop.Security.Filters.SessionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, JwtFilter jwtFilter, SessionFilter sessionFilter) throws Exception {

    return http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/auth/login",
                        "/auth/refresh",
                        "/actuator/health",
                        "/actuator/prometheus",
                        "/actuator/info")
                    .permitAll() // for testing only if production or when pass to next stip ( which
                    // is  separate domains to MS or continue Observability ) most
                    // protect actuators
                    .anyRequest()
                    .permitAll()) // the authorization is on Methods (to can separate the domains in
        // future)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(sessionFilter, JwtFilter.class)
        .build();
  }

  @Bean
  public CompromisedPasswordChecker cpc() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
