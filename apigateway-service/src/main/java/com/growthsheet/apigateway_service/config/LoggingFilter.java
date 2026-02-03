package com.growthsheet.apigateway_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long start = System.currentTimeMillis();

        log.info("➡️ {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath());

        return chain.filter(exchange)
                .doOnSuccess(done -> {
                    long time = System.currentTimeMillis() - start;
                    log.info("⬅️ {} {} ({} ms)",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI().getPath(),
                            time);
                })
                .doOnError(err -> {
                    long time = System.currentTimeMillis() - start;
                    log.error("❌ {} {} ({} ms) : {}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI().getPath(),
                            time,
                            err.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
