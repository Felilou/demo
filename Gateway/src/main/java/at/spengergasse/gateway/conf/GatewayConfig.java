package at.spengergasse.gateway.conf;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user", r -> r.path("/auth/**")
                        .uri("lb://playerservice"))
                .route("games", r -> r.path("/games/**")
                        .uri("lb://gameservice"))
                .build();
    }

}
