package com.example.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
//@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
    private final DataSource dataSource;
    private final CustomUserDetailService customUserDetailService;
    private final CustomAuthDetails customAuthDetails;
//    private final StudentManager studentManager;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //????????? ????????? ?????????
        // ??????, csrf ??????
        http
                .headers().disable();
//                .csrf().disable();
        // request ?????? ??????
        http.authorizeRequests(request -> request
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
        );
        // ????????? ??????
        http.formLogin().loginPage("/login").permitAll()
                .defaultSuccessUrl("/", false)
                .failureUrl("/login-error")
                .authenticationDetailsSource(customAuthDetails);
        // ???????????? ??????
        http.logout().logoutSuccessUrl("/");
        // Exception ??????
        http.exceptionHandling().accessDeniedPage("/access-denied");

        // remember_me ???????????????
        http.rememberMe()
                .key("123456789")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(customUserDetailService)
                .tokenValiditySeconds(60 * 60 * 24 * 30); // 30?????? ????????????

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // FEAT : Security ????????? ???????????? css, js ?????? ?????? ???????????? ???????????? ??????
        return web -> web.ignoring()
                .antMatchers("/swagger-ui/")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
}
