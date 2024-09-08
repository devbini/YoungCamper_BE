package com.youngcamp.server.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${spring.security.users.dev.username}")
  private String devUsername;

  @Value("${spring.security.users.dev.password}")
  private String devPassword;

  @Value("${spring.security.users.admin.username}")
  private String adminUsername;

  @Value("${spring.security.users.admin.password}")
  private String adminPassword;

  private final HttpSessionIdResolver httpSessionIdResolver;

  public SecurityConfig(HttpSessionIdResolver httpSessionIdResolver) {
    this.httpSessionIdResolver = httpSessionIdResolver;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(withDefaults())
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/api/swagger-ui/**", "/v3/api-docs/**")
                    .hasRole("DEV")
                    .anyRequest()
                    .permitAll())
        .formLogin(
            formLogin ->
                formLogin.loginPage("/api/login").loginProcessingUrl("/api/login").permitAll())
        .httpBasic(withDefaults())
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/admin/logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID", "ADMINID")
                    .logoutSuccessUrl("/"))
        .sessionManagement(
            sessionManagement -> {
              sessionManagement.maximumSessions(1).maxSessionsPreventsLogin(false);
              sessionManagement.sessionFixation().newSession();
              sessionManagement.invalidSessionUrl("/");
              sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
            })
        .securityContext(
            securityContext ->
                securityContext.securityContextRepository(
                    new HttpSessionSecurityContextRepository()));

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails devUser =
        User.builder()
            .username(devUsername)
            .password(passwordEncoder.encode(devPassword))
            .roles("DEV")
            .build();

    UserDetails adminUser =
        User.builder()
            .username(adminUsername)
            .password(passwordEncoder.encode(adminPassword))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(devUser, adminUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
