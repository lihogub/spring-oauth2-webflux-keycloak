package ru.lihogub.springoauth2webfluxkeycloak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /*
     * Creates default WebClient bean
     */
    @Bean
    WebClient webClientDefault() {
        return WebClient.create();
    }

    /*
     * Created OAuth2 authorized WebClient using "keycloak" configuration in application.yaml
     */
    @Bean("oauth-client")
    WebClient webClientOauthAuthorized(ReactiveClientRegistrationRepository clientRegistrations,
                        ServerOAuth2AuthorizedClientRepository authorizedClients) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
        oauth.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(oauth)
                .build();
    }

}
