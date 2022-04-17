package ru.lihogub.springoauth2webfluxkeycloak.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.lihogub.springoauth2webfluxkeycloak.handler.ExampleHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class ExampleRouter {

    @Bean
    RouterFunction<ServerResponse> route(ExampleHandler exampleHandler) {
        return RouterFunctions
                .route(GET("/public"), exampleHandler::endpointPublic)
                .andRoute(GET("/private"), exampleHandler::endpointPrivate)
                .andRoute(GET("/public/private"), exampleHandler::endpointPublicToPrivate)
                .andRoute(GET("/private/public"), exampleHandler::endpointPrivateToPublic)
                .andRoute(GET("/private/private"), exampleHandler::endpointPrivateToPrivateWithUsersCredentials);
    }

}
