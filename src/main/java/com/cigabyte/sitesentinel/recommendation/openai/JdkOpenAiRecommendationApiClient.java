package com.cigabyte.sitesentinel.recommendation.openai;

import com.cigabyte.sitesentinel.recommendation
        .RiskRemediationAiRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class JdkOpenAiRecommendationApiClient
        implements OpenAiRecommendationApiClient {

    private static final String RESPONSES_OPERATION =
            "/responses";

    private final OpenAiRecommendationProperties properties;

    private final OpenAiRecommendationRequestBodyFactory
            requestBodyFactory;

    private final OpenAiRecommendationResponseParser
            responseParser;

    private final HttpClient httpClient;

    @Autowired
    public JdkOpenAiRecommendationApiClient(
            OpenAiRecommendationProperties properties,
            OpenAiRecommendationRequestBodyFactory
                    requestBodyFactory,
            OpenAiRecommendationResponseParser responseParser
    ) {
        this(
                properties,
                requestBodyFactory,
                responseParser,
                createHttpClient(properties)
        );
    }

    JdkOpenAiRecommendationApiClient(
            OpenAiRecommendationProperties properties,
            OpenAiRecommendationRequestBodyFactory
                    requestBodyFactory,
            OpenAiRecommendationResponseParser responseParser,
            HttpClient httpClient
    ) {
        this.properties =
                Objects.requireNonNull(
                        properties,
                        "OpenAI recommendation properties "
                                + "are required."
                );

        this.requestBodyFactory =
                Objects.requireNonNull(
                        requestBodyFactory,
                        "OpenAI recommendation request body "
                                + "factory is required."
                );

        this.responseParser =
                Objects.requireNonNull(
                        responseParser,
                        "OpenAI recommendation response "
                                + "parser is required."
                );

        this.httpClient =
                Objects.requireNonNull(
                        httpClient,
                        "HTTP client is required."
                );
    }

    @Override
    public OpenAiRecommendationApiResult
    generateRecommendation(
            RiskRemediationAiRequest request
    ) {
        RiskRemediationAiRequest requiredRequest =
                Objects.requireNonNull(
                        request,
                        "Risk remediation AI request "
                                + "is required."
                );

        try {
            String requestBody =
                    requestBodyFactory.create(
                            requiredRequest
                    );

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    createResponsesUri()
                            )
                            .timeout(
                                    Duration.ofSeconds(
                                            properties
                                                    .getRequestTimeoutSeconds()
                                    )
                            )
                            .header(
                                    "Authorization",
                                    "Bearer "
                                            + properties.getApiKey()
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Accept",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    requestBody,
                                                    StandardCharsets.UTF_8
                                            )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers
                                    .ofString(
                                            StandardCharsets.UTF_8
                                    )
                    );

            return classifyResponse(
                    response
            );

        } catch (HttpTimeoutException exception) {
            return OpenAiRecommendationApiResult
                    .timeout();

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            return OpenAiRecommendationApiResult
                    .interrupted();

        } catch (IOException exception) {
            return OpenAiRecommendationApiResult
                    .failure();

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | SecurityException exception) {

            return OpenAiRecommendationApiResult
                    .failure();
        }
    }

    private OpenAiRecommendationApiResult classifyResponse(
            HttpResponse<String> response
    ) {
        int httpStatusCode =
                response.statusCode();

        if (httpStatusCode >= 200
                && httpStatusCode <= 299) {

            return responseParser
                    .parseSuccessfulResponse(
                            httpStatusCode,
                            response.body()
                    );
        }

        if (httpStatusCode == 401
                || httpStatusCode == 403) {

            return OpenAiRecommendationApiResult
                    .authenticationFailed(
                            httpStatusCode
                    );
        }

        if (httpStatusCode == 429) {
            return OpenAiRecommendationApiResult
                    .rateLimited(
                            httpStatusCode
                    );
        }

        if (httpStatusCode >= 500
                && httpStatusCode <= 599) {

            return OpenAiRecommendationApiResult
                    .providerUnavailable(
                            httpStatusCode
                    );
        }

        return OpenAiRecommendationApiResult
                .requestRejected(
                        httpStatusCode
                );
    }

    private URI createResponsesUri() {
        return URI.create(
                properties.getNormalizedApiBaseUrl()
                        + RESPONSES_OPERATION
        );
    }

    private static HttpClient createHttpClient(
            OpenAiRecommendationProperties properties
    ) {
        OpenAiRecommendationProperties
                requiredProperties =
                Objects.requireNonNull(
                        properties,
                        "OpenAI recommendation properties "
                                + "are required."
                );

        return HttpClient.newBuilder()
                .connectTimeout(
                        Duration.ofSeconds(
                                requiredProperties
                                        .getConnectTimeoutSeconds()
                        )
                )
                .followRedirects(
                        HttpClient.Redirect.NEVER
                )
                .build();
    }
}