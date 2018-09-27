package com.jeddit.backend.authentification.config

import com.jeddit.backend.authentification.security.JwtAuthenticationEntryPoint
import com.jeddit.backend.authentification.security.JwtAuthorizationTokenFilter
import com.jeddit.backend.authentification.security.service.JwtUserDetailsService
//import com.jeddit.backend.authentification.security.service.JwtUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private val unauthorizedHandler: JwtAuthenticationEntryPoint? = null

    @Autowired
    private val jwtUserDetailsService: JwtUserDetailsService? = null

    // Custom JWT based security filter
    @Autowired
    internal var authenticationTokenFilter: JwtAuthorizationTokenFilter? = null

    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @Value("\${jwt.route.authentication.path}")
    private val authenticationPath: String? = null

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<UserDetailsService>(jwtUserDetailsService)
                .passwordEncoder(passwordEncoderBean())
    }

    @Bean
    fun passwordEncoderBean(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()

                // Un-secure H2 Database
//                .antMatchers("/h2-console/**/**").permitAll()

                .antMatchers(
                        "/auth/**",
                        "/api/post/*/comments",
                        "/api/subjeddit/*/info").permitAll()
                .anyRequest().authenticated()

        httpSecurity
                .addFilterBefore(authenticationTokenFilter!!, UsernamePasswordAuthenticationFilter::class.java)

        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl()
    }

    @Throws(Exception::class)
    override fun configure(web: WebSecurity?) {
        // AuthenticationTokenFilter will ignore the below paths
        web!!
                .ignoring()
                .antMatchers(
                        HttpMethod.POST,
                        authenticationPath!!
                )

                // allow anonymous resource requests
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                )
//
//                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
//                .and()
//                .ignoring()
//                .antMatchers("/h2-console/**/**")
    }
}
