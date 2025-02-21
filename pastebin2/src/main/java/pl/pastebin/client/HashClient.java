package pl.pastebin.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public final class HashClient {
    private final WebClient webClient;

    private Mono<String> hashConnection() {
        return webClient
                .get()
                .uri("/api/get")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getHash() {
        return hashConnection()
                .doOnSuccess(hash -> log.info("Received hash: {}", hash))
                .doOnError(error -> log.info("Error: {}", error.getMessage()))
                .doOnTerminate(() -> log.info("Request completed"));
    }


}
