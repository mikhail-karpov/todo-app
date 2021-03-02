package com.mikhailkarpov.todoclient.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class OAuth2Config extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .csrf().disable()
            .authorizeRequests().anyRequest().authenticated().and()
            .oauth2Login();
        //@formatter:on
    }
}
