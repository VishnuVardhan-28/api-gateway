package com.microservice.app.api_gateway;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.WebFilter;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/product/**")
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/api/order/**")
                        .uri("lb://order-service"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri("lb://inventory-service"))
                .route("discovery-server",r -> r.path("/eureka/web").filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))
                .route("discovery-server-static",r -> r.path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }

    @Bean
    public WebFilter traceLogger(Tracer tracer) {
        return (exchange, chain) -> {
            Span span = tracer.currentSpan();
            if (span != null) {
                System.out.println(" Active Trace ID: " + span.context().traceId());
            } else {
                System.out.println(" NO SPAN - Tracing is not active");
            }
            return chain.filter(exchange);
        };
    }

}
