package com.cigabyte.sitesentinel.notification.delivery;

import java.util.Arrays;

public final class TelegramDocumentUploadRequest {

    private static final int MAX_CHAT_ID_LENGTH =
            255;

    private static final int MAX_FILE_NAME_LENGTH =
            255;

    private static final int MAX_CONTENT_TYPE_LENGTH =
            100;

    private static final int MAX_CAPTION_LENGTH =
            1024;

    private final String chatId;

    private final String fileName;

    private final String contentType;

    private final byte[] documentBytes;

    private final String caption;

    public TelegramDocumentUploadRequest(
            String chatId,
            String fileName,
            String contentType,
            byte[] documentBytes,
            String caption
    ) {
        this.chatId =
                normalizeRequiredSingleLineText(
                        chatId,
                        "Telegram chat ID",
                        MAX_CHAT_ID_LENGTH
                );

        this.fileName =
                normalizeFileName(
                        fileName
                );

        this.contentType =
                normalizeRequiredSingleLineText(
                        contentType,
                        "Document content type",
                        MAX_CONTENT_TYPE_LENGTH
                );

        this.documentBytes =
                copyRequiredDocumentBytes(
                        documentBytes
                );

        this.caption =
                normalizeCaption(
                        caption
                );
    }

    private String normalizeFileName(
            String value
    ) {
        String normalizedValue =
                normalizeRequiredSingleLineText(
                        value,
                        "Document file name",
                        MAX_FILE_NAME_LENGTH
                );

        if (normalizedValue.contains("/")
                || normalizedValue.contains("\\")
                || normalizedValue.contains("\"")) {

            throw new IllegalArgumentException(
                    "Document file name contains "
                            + "unsupported characters."
            );
        }

        return normalizedValue;
    }

    private String normalizeRequiredSingleLineText(
            String value,
            String fieldName,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        String normalizedValue =
                value.trim();

        if (normalizedValue.contains("\r")
                || normalizedValue.contains("\n")) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must contain a single line."
            );
        }

        if (normalizedValue.length()
                > maximumLength) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }

        return normalizedValue;
    }

    private byte[] copyRequiredDocumentBytes(
            byte[] value
    ) {
        if (value == null || value.length == 0) {
            throw new IllegalArgumentException(
                    "Document bytes are required."
            );
        }

        return Arrays.copyOf(
                value,
                value.length
        );
    }

    private String normalizeCaption(
            String value
    ) {
        if (value == null) {
            return "";
        }

        String normalizedValue =
                value.trim();

        if (normalizedValue.length()
                > MAX_CAPTION_LENGTH) {

            throw new IllegalArgumentException(
                    "Document caption must not exceed "
                            + MAX_CAPTION_LENGTH
                            + " characters."
            );
        }

        return normalizedValue;
    }

    public String getChatId() {
        return chatId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getDocumentBytes() {
        return Arrays.copyOf(
                documentBytes,
                documentBytes.length
        );
    }

    public String getCaption() {
        return caption;
    }
}