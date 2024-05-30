package br.com.everdev.nameresolutionispserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/isp")
public class ISPController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @PostMapping("/validacao")
    public Mono<String> validarEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String validacaoAppUrl = discoveryClient.getInstances("VALIDACAO-APP").stream()
                .findFirst()
                .map(instance -> instance.getUri().toString())
                .orElseThrow(() -> new RuntimeException("App não encontrado."));

        return webClientBuilder.build()
                .post()
                .uri(validacaoAppUrl + "/validar-email")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);
    }
}
