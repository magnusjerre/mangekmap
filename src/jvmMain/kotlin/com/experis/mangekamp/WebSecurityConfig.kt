package com.experis.mangekamp

import com.experis.mangekamp.repositories.AdminUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Service


@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer { web -> web.ignoring().antMatchers("/resources/**") }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests()
            .antMatchers("/multiplatform-**","/static/**", "/resources/**", "/js/**", "/resources/js/**", "/public/**")
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/index.html")
            .loginProcessingUrl("/perform_login")
            .defaultSuccessUrl("/homepage.html", true)
            .failureUrl("/index.html?error=true")
            .permitAll()
        return http.build()
    }
}

@Service
class MangekampUserDetailsService(
    private val adminUserRepository: AdminUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        username ?: throw IllegalArgumentException("Can't look for user with username null")
        return adminUserRepository.findByUsername(username)?.let { adminUser ->
            User(
                adminUser.username,
                adminUser.passwordDigest,
                mutableListOf()
            )
        } ?: throw UsernameNotFoundException(username)
    }
}
