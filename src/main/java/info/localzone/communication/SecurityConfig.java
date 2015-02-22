package info.localzone.communication;

import info.localzone.communication.controller.UserCreateFormValidator;
import info.localzone.communication.repo.UserRepository;
import info.localzone.communication.service.security.UserService;
import info.localzone.communication.service.security.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired UserRepository userRepository;
    @Bean
    UserService userService () {
    	return new UserServiceImpl(userRepository);
    }
    
    @Bean UserCreateFormValidator userCreateFormValidator () {
    	return new UserCreateFormValidator(userService());
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	  http.authorizeRequests()
          .antMatchers("/", "/public/**,/css/**,/img/**,/js/**,/bootstrap/**,/*.js").permitAll()
          .antMatchers("/users/**").hasAuthority("ADMIN")
          .antMatchers("/restricted/**").fullyAuthenticated()
          .and()
          .formLogin()
          .loginPage("/login")
          .failureUrl("/login?error")
          .usernameParameter("email")
          .permitAll()
          .and()
          .logout()
          .logoutUrl("/logout")
          .deleteCookies("remember-me")
          .logoutSuccessUrl("/")
          .permitAll()
          .and()
          .rememberMe();
    }

    
 /*   protected void configure(HttpSecurity http) throws Exception {
  	  http.authorizeRequests()       
        .antMatchers("/", "/public/**,/css/**,/img/**,/js/**,/bootstrap/**,/*.js").permitAll()
        .and()
        .formLogin()
        .loginPage("/login");
        
  }*/
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

}
