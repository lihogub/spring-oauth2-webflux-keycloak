package ru.lihogub.springoauth2webfluxkeycloak.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
public class ExampleHandler {

    private final WebClient defaultWebClient;
    private final WebClient oAuth2AuthorizedWebClient;

    public ExampleHandler(
            @Qualifier("webClientDefault") WebClient defaultWebClient,
            @Qualifier("oauth-client") WebClient oAuth2AuthorizedWebClient
    ) {
        this.defaultWebClient = defaultWebClient;
        this.oAuth2AuthorizedWebClient = oAuth2AuthorizedWebClient;
    }

    /*
    * Public endpoint
    * Allows performing anonymous requests
    */
    public Mono<ServerResponse> endpointPublic(ServerRequest serverRequest) {
        return Mono.just("/public")
                .flatMap(s -> ServerResponse.ok().bodyValue(s));
    }

    /*
     * Private endpoint
     * Requires users JWT
     */
    public Mono<ServerResponse> endpointPrivate(ServerRequest serverRequest) {
        return Mono.just("/private")
                .flatMap(s -> ServerResponse.ok().bodyValue(s));
    }

    /*
     * Private endpoint
     * Performs nested call to private endpoint
     * Passes users JWT to nested request
     */
    public Mono<ServerResponse> endpointPrivateToPrivateWithUsersCredentials(ServerRequest serverRequest) {
        return defaultWebClient
                .get()
                .uri("http://localhost:8080/private")
                .headers(h -> h.set(
                        HttpHeaders.AUTHORIZATION,
                        serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION))
                )
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> "user -[user_credentials]-> \"/private\" -[user_credentials]->" + s)
                .flatMap(s -> ServerResponse.ok().bodyValue(s));
    }

    /*
     * Private endpoint
     * Performs nested call to public endpoint
     */
    public Mono<ServerResponse> endpointPrivateToPublic(ServerRequest serverRequest) {
        return defaultWebClient.get()
                .uri("http://localhost:8080/public")
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> "user -[user_credentials]-> \"/private\" -[anonymous]->" + s)
                .flatMap(s -> ServerResponse.ok().bodyValue(s));
    }

    /*
     * Public endpoint
     * Performs nested call to private endpoint using OAuth2 client_credentials
     */
    public Mono<ServerResponse> endpointPublicToPrivate(ServerRequest serverRequest) {
        return oAuth2AuthorizedWebClient.get()
                .uri("http://localhost:8080/private")
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> "user -[anonymous]-> \"/public\" -[client_credentials]->" + s)
                .flatMap(s -> ServerResponse.ok().bodyValue(s));
    }

}
