package com.smanager;

import com.smanager.dao.models.UserRoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/webjars/**").permitAll();
        http
                .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles(UserRoles.ADMIN.getRole())
                .build();

        UserDetails student = User.withDefaultPasswordEncoder()
                .username("student")
                .password("student")
                .roles(UserRoles.STUDENT.getRole())
                .build();

        UserDetails teacher = User.withDefaultPasswordEncoder()
                .username("teacher")
                .password("teacher")
                .roles(UserRoles.TEACHER.getRole())
                .build();

        InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();
        userManager.createUser(admin);
        userManager.createUser(student);
        userManager.createUser(teacher);

        return userManager;
    }
}
