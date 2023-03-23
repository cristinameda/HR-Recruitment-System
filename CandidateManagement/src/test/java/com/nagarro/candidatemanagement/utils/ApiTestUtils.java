package com.nagarro.candidatemanagement.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@Component
public class ApiTestUtils {
    @Value("${user-management.user-validation}")
    private String userValidationEndPoint;
    @Value("${user-management.token-validation}")
    private String tokenValidationEndpoint;

    public ApiTestUtils() {
    }

    public void mockUserEmailEndpointStatusCode200(Boolean response) {
        stubFor(WireMock.post(urlPathEqualTo(userValidationEndPoint))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"valid\": " + response + "}")));
    }

    public void mockUserEmailEndpointStatusCode400() {
        stubFor(WireMock.post(urlPathEqualTo(userValidationEndPoint))
                .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));
    }

    public void mockUserServiceResponse(HttpStatus status) {
        stubFor(WireMock.post(urlPathMatching(tokenValidationEndpoint))
                .willReturn(aResponse()
                        .withStatus(status.value())));
    }

    public String getUserValidationEndPoint() {
        return userValidationEndPoint;
    }

    public String getTokenValidationEndpoint() {
        return tokenValidationEndpoint;
    }
}
