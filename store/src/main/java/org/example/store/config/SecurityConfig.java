package org.example.store.config;

import org.example.store.security.XUserIdAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            // 允许跨域
            .cors(Customizer.withDefaults())
            // 不用 Session
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 权限规则
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    //  商品相关公共接口
                    .requestMatchers(HttpMethod.POST, "/products/list").permitAll()
                    .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                    //  评价相关公共接口
                    .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()

                    // 登录/注册/用户相关公共接口
                    .requestMatchers("/auth/**", "/api/auth/**",
                                   "/users/register", "/users/login", "/users/login/code", "/users/password/reset",
                                   "/api/users/register", "/api/users/login", "/api/users/login/code", "/api/users/password/reset",
                                   "/users/me", "/api/users/me", "/users/current", "/api/users/current")
                            .permitAll()

                    // 短信验证码接口
                    .requestMatchers("/sms/**").permitAll()

                    // 其他所有请求都需要认证
                    .anyRequest().authenticated()
            )
            // 未登录/无权限
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, resp, e) -> {
                        resp.setStatus(401);
                        resp.setContentType("application/json;charset=UTF-8");
                        resp.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
                    })
            );

    http.addFilterBefore(new XUserIdAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
}

}
