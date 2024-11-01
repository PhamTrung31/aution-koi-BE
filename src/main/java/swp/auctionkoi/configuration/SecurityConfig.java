package swp.auctionkoi.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final String[] PUBLIC_ENDPOINTS = {
            "/users", "/staffs/**", "/auth/**", "/auth/token", "/auth/introspect",
            "/auth/logout", "/auction/send-request", "/auctions/join",
            "/auctions/end/{auctionId}", "/auction/update/{auctionRequestId}",
            "/auction/cancel/{auctionRequestId}", "/users/create",
            "/users/create", "/api/payment/vnpay-return", "/api/wallet/withdraw",
            "/auction/reject/{auctionRequestId}", "/auction/booking", "/auction/view-all-requests",
            "/auction/view-request-detail/{auctionRequestId}",
            "/auction/view-all-breeder-requests/{breederId}",
            "/deliveries/status", "/api/files/upload", "/api/koifish/upload/{koiId}",
            "/deliveries/{deliveryId}", "/users/{id}/avatar", "/vnpay/submitOrder",
            "/vnpay/", "/vnpay/vnpay-payment-return","/wallet/{userId}","/hello",
            "/payment/requestwithdraw","/google/hello","/auctions/check-participation",
            "/auctions/manager-review","/auth/login-with-google-token",
            "/login/oauth2/code/google",
            "v2/api-docs",
            "v3/api-docs",
            "v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };


    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private CustomJwtDecoder customJwtDecoder;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests(request ->

                request.requestMatchers(
                                "/api/v1/auth/**",
                                "v2/api-docs",
                                "v3/api-docs",
                                "v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"


                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/staffs").hasAuthority("ROLE_STAFF")
                        .requestMatchers(HttpMethod.GET, "/manager/**").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/manager/**").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/manager/**").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/manager/**").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PUT, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/staffs").hasAnyAuthority("ROLE_STAFF")
                        .requestMatchers(HttpMethod.GET, "/manager").hasAnyAuthority("ROLE_MANAGER")
                        .anyRequest().authenticated())

                .oauth2Login(oauth2 ->
                        oauth2.defaultSuccessUrl("http://localhost:5173",true))
//                .oauth2Login(withDefaults())
//                .formLogin(withDefaults())


                .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(jwtDecoder())
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())

        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public HttpFirewall allowUrlEncodedNewlineHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);  // Cho phép ký tự %
        firewall.setAllowUrlEncodedSlash(true);    // Cho phép dấu /
        firewall.setAllowBackSlash(true);          // Cho phép dấu \
        firewall.setAllowSemicolon(true);          // Cho phép dấu ;
        firewall.setAllowUrlEncodedDoubleSlash(true); // Cho phép //
        firewall.setAllowUrlEncodedPeriod(true);   // Cho phép dấu .ấu :
        firewall.setAllowUrlEncodedLineFeed(true);  // Cho phép ký tự xuống dòng
        return firewall;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Nếu vai trò trong token có tiền tố "ROLE_"
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Tên trường trong token chứa vai trò

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return converter;
    }


    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedOrigin("https://aution-koi-fe.vercel.app");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    // Overriding method to add CORS mappings
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://aution-koi-fe.vercel.app")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Methods", "Access-Control-Allow-Headers")
                .maxAge(1440000);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}

