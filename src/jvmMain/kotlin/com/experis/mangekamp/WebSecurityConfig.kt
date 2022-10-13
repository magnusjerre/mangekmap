package com.experis.mangekamp

import com.experis.mangekamp.repositories.AdminUserRepository
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Service


@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer { web -> web.ignoring().antMatchers("/resources/**") }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/**").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/**").authenticated()
            .antMatchers(HttpMethod.DELETE, "/api/**").authenticated()
            .antMatchers(HttpMethod.PATCH, "/api/**").authenticated()
            .antMatchers(HttpMethod.GET, "/api/**").permitAll()
            .antMatchers(
                "/multiplatform-**",
                "/static/**",
                "/resources/**",
                "/js/**",
                "/resources/js/**",
                "/public/**",
                "/index*",
                "/index.html",
                "/"
            )
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/")
            .loginProcessingUrl("/loginprocessingurl")
            .successHandler { _, response, _ -> response!!.status = HttpStatus.OK.value() }
            .failureHandler { _, response, _ -> response!!.status = HttpStatus.UNAUTHORIZED.value() }
            .permitAll()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { _, response, exception ->
                response!!.status = HttpStatus.FORBIDDEN.value()
            }
            .accessDeniedHandler { _, response, _ ->
                response!!.status = HttpStatus.UNAUTHORIZED.value()
            }
        return http.build()
    }
}

@Service
class MangekampUserDetailsService(
    private val adminUserRepository: AdminUserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        return adminUserRepository.findByUsername(username)?.let {
            User.builder()
                .username(it.username)
                .password(it.passwordDigest)
                .roles("USER")
                .build()
        } ?: throw UsernameNotFoundException(username)
    }
}
