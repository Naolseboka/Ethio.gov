package et.gov.ethgov.licensing.adapter.in.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class LicensingSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/prometheus")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/**")
                        .hasAnyAuthority("SCOPE_licensing.read", "SCOPE_licensing.write")
                        .requestMatchers(HttpMethod.POST, "/v1/**")
                        .hasAuthority("SCOPE_licensing.write")
                        .requestMatchers(HttpMethod.PUT, "/v1/**")
                        .hasAuthority("SCOPE_licensing.write")
                        .anyRequest()
                        .authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/v1/**", "/actuator/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Profile("!test")
    JwtDecoder jwtDecoder(@Value("${ethgov.identity.issuer}") String issuer) {
        return JwtDecoders.fromIssuerLocation(issuer);
    }
}
