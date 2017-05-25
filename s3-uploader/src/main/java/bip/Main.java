package bip;

import java.time.Duration;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;


@EnableCircuitBreaker
@SpringBootApplication
public class Main {
   
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}