package com.phannhubao.config;

import com.phannhubao.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

            @Override
            public String encode(CharSequence rawPassword) {
                return bcrypt.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (encodedPassword == null || encodedPassword.isEmpty()) {
                    return false;
                }
                // Nếu mật khẩu đã mã hóa dạng BCrypt (bắt đầu bằng $2a$, $2b$ hoặc $2y$)
                if (encodedPassword.startsWith("$2a$") || 
                    encodedPassword.startsWith("$2b$") || 
                    encodedPassword.startsWith("$2y$")) {
                    try {
                        return bcrypt.matches(rawPassword, encodedPassword);
                    } catch (IllegalArgumentException e) {
                        return rawPassword.toString().equals(encodedPassword);
                    }
                }
                // Hỗ trợ so khớp trực tiếp dạng plaintext (dành cho tk tạo tay từ pgAdmin)
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
