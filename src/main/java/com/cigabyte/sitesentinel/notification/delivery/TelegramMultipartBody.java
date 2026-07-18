package com.cigabyte.sitesentinel.notification.delivery;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

final class TelegramMultipartBody {

    private static final String LINE_BREAK =
            "\r\n";

    private static final String BOUNDARY_PREFIX =
            "SiteSentinelTelegramBoundary";

    private final String boundary;

    private final byte[] bodyBytes;

    private TelegramMultipartBody(
            String boundary,
            byte[] bodyBytes
    ) {
        this.boundary = boundary;

        this.bodyBytes =
                Arrays.copyOf(
                        bodyBytes,
                        bodyBytes.length
                );
    }

    static TelegramMultipartBody from(
            TelegramDocumentUploadRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Telegram document upload request is required."
            );
        }

        String boundary =
                BOUNDARY_PREFIX
                        + UUID.randomUUID()
                        .toString()
                        .replace("-", "");

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        writeTextPart(
                outputStream,
                boundary,
                "chat_id",
                request.getChatId()
        );

        if (!request.getCaption().isBlank()) {
            writeTextPart(
                    outputStream,
                    boundary,
                    "caption",
                    request.getCaption()
            );
        }

        writeDocumentPart(
                outputStream,
                boundary,
                request
        );

        writeUtf8(
                outputStream,
                "--"
                        + boundary
                        + "--"
                        + LINE_BREAK
        );

        return new TelegramMultipartBody(
                boundary,
                outputStream.toByteArray()
        );
    }

    String getContentTypeHeaderValue() {
        return "multipart/form-data; boundary="
                + boundary;
    }

    String getBoundary() {
        return boundary;
    }

    byte[] getBodyBytes() {
        return Arrays.copyOf(
                bodyBytes,
                bodyBytes.length
        );
    }

    private static void writeTextPart(
            ByteArrayOutputStream outputStream,
            String boundary,
            String fieldName,
            String fieldValue
    ) {
        writeUtf8(
                outputStream,
                "--"
                        + boundary
                        + LINE_BREAK
                        + "Content-Disposition: form-data; "
                        + "name=\""
                        + fieldName
                        + "\""
                        + LINE_BREAK
                        + LINE_BREAK
                        + fieldValue
                        + LINE_BREAK
        );
    }

    private static void writeDocumentPart(
            ByteArrayOutputStream outputStream,
            String boundary,
            TelegramDocumentUploadRequest request
    ) {
        writeUtf8(
                outputStream,
                "--"
                        + boundary
                        + LINE_BREAK
                        + "Content-Disposition: form-data; "
                        + "name=\"document\"; filename=\""
                        + request.getFileName()
                        + "\""
                        + LINE_BREAK
                        + "Content-Type: "
                        + request.getContentType()
                        + LINE_BREAK
                        + LINE_BREAK
        );

        outputStream.writeBytes(
                request.getDocumentBytes()
        );

        writeUtf8(
                outputStream,
                LINE_BREAK
        );
    }

    private static void writeUtf8(
            ByteArrayOutputStream outputStream,
            String value
    ) {
        outputStream.writeBytes(
                value.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }
}