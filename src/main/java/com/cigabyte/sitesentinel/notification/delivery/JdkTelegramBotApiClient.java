package com.cigabyte.sitesentinel.notification.delivery;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class JdkTelegramBotApiClient implements TelegramBotApiClient {

    private final TelegramDeliveryProperties telegramDeliveryProperties;

    private final HttpClient httpClient;

    public JdkTelegramBotApiClient(
            TelegramDeliveryProperties telegramDeliveryProperties
    ) {
        this.telegramDeliveryProperties = telegramDeliveryProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(
                        telegramDeliveryProperties.getConnectTimeoutSeconds()
                ))
                .build();
    }

    @Override
    public TelegramBotApiResponse getMe() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildTelegramApiUri("getMe"))
                .timeout(Duration.ofSeconds(
                        telegramDeliveryProperties.getRequestTimeoutSeconds()
                ))
                .GET()
                .build();

        return execute(request);
    }

    @Override
    public TelegramBotApiResponse sendMessage(
            String chatId,
            String message
    ) {
        String requestBody = buildSendMessageRequestBody(
                chatId,
                message
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildTelegramApiUri("sendMessage"))
                .timeout(Duration.ofSeconds(
                        telegramDeliveryProperties.getRequestTimeoutSeconds()
                ))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return execute(request);
    }

    @Override
    public TelegramBotApiResponse sendDocument(
            TelegramDocumentUploadRequest request
    ) {
        TelegramMultipartBody multipartBody =
                TelegramMultipartBody.from(
                        request
                );

        HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .uri(
                                buildTelegramApiUri(
                                        "sendDocument"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(
                                        telegramDeliveryProperties
                                                .getRequestTimeoutSeconds()
                                )
                        )
                        .header(
                                "Content-Type",
                                multipartBody
                                        .getContentTypeHeaderValue()
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofByteArray(
                                                multipartBody
                                                        .getBodyBytes()
                                        )
                        )
                        .build();

        return execute(
                httpRequest
        );
    }

    private TelegramBotApiResponse execute(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return new TelegramBotApiResponse(
                    response.statusCode(),
                    response.body()
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new TelegramBotApiClientException(
                    "Telegram Bot API request was interrupted.",
                    exception
            );

        } catch (IOException exception) {
            throw new TelegramBotApiClientException(
                    "Telegram Bot API request failed.",
                    exception
            );

        } catch (IllegalArgumentException exception) {
            throw new TelegramBotApiClientException(
                    "Telegram Bot API request could not be created.",
                    exception
            );
        }
    }

    private URI buildTelegramApiUri(String operation) {
        String apiUrl =
                telegramDeliveryProperties.getNormalizedApiBaseUrl()
                        + "/bot"
                        + telegramDeliveryProperties.getBotToken()
                        + "/"
                        + operation;

        return URI.create(apiUrl);
    }

    private String buildSendMessageRequestBody(
            String chatId,
            String message
    ) {
        return "chat_id="
                + encode(chatId)
                + "&text="
                + encode(message)
                + "&disable_web_page_preview=true";
    }

    private String encode(String value) {
        return URLEncoder.encode(
                value == null ? "" : value,
                StandardCharsets.UTF_8
        );
    }
}