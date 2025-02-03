package ru.smax.social.network.dialog;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor traceIdRequestInterceptor() {
        return template -> {
            Optional.ofNullable(MDC.get("traceId")).ifPresent(traceId -> template.header("X-B3-TraceId", traceId));
            Optional.ofNullable(MDC.get("spanId")).ifPresent(traceId -> template.header("X-B3-SpanId", traceId));
        };
    }
}
