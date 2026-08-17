package com.cigabyte.sitesentinel.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties(
        SiteSentinelSecurityProperties.class
)
public class SiteSentinelSecurityConfiguration {

    private static final String OPERATOR_ROLE =
            "OPERATOR";

    @Bean
    SecurityFilterChain siteSentinelSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http.authorizeHttpRequests(
                authorization ->
                        authorization
                                .requestMatchers(
                                        "/login",
                                        "/css/**",
                                        "/error"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
        );

        http.formLogin(
                formLogin ->
                        formLogin
                                .loginPage(
                                        "/login"
                                )
                                .defaultSuccessUrl(
                                        "/",
                                        true
                                )
                                .permitAll()
        );

        http.logout(
                logout ->
                        logout
                                .logoutSuccessUrl(
                                        "/login?logout"
                                )
                                .permitAll()
        );

        return http.build();
    }

    @Bean
    PasswordEncoder siteSentinelPasswordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService siteSentinelUserDetailsService(
            SiteSentinelSecurityProperties properties,
            PasswordEncoder passwordEncoder
    ) {
        UserDetails operator =
                User.withUsername(
                                properties.getUsername()
                        )
                        .password(
                                passwordEncoder.encode(
                                        properties.getPassword()
                                )
                        )
                        .roles(
                                OPERATOR_ROLE
                        )
                        .build();

        return new InMemoryUserDetailsManager(
                operator
        );
    }
}